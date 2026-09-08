package com.teamfis.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisMotion
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSize
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/** 회원 상태 — 회원 목록을 가르는 세 갈래. */
enum class MemberStatus(val label: String) {
    Active("활성"),
    Holding("홀딩"),
    Expired("만료"),
}

/** 회원 한 명. 데이터가 붙기 전까지는 화면에서 만들어 넣는다. */
data class Member(
    val name: String,
    val status: MemberStatus,
    /** `12/30회차` — 진행한 회차 / 등록 회차 */
    val progress: String,
    /** 이름 밑 한 줄 — 마지막 수업일·홀딩 시작일 같은 것 */
    val detail: String,
)

/**
 * 회원 필터 — 활성 · 홀딩 · 만료.
 *
 * **고른 것을 다시 누르면 풀린다.** 아무것도 안 골랐을 때가 보유 회원 전체이고,
 * 그게 이 화면의 기본이다. `전체` 칸을 따로 두지 않는 이유다.
 *
 * 칩마다 숫자를 달아 **고르지 않고도 갈래별 규모**가 보이게 한다.
 */
@Composable
fun MemberFilterBar(
    selected: MemberStatus?,
    counts: Map<MemberStatus, Int>,
    onSelect: (MemberStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
    ) {
        MemberStatus.entries.forEach { status ->
            CountChip(
                label = status.label,
                count = counts[status] ?: 0,
                selected = selected == status,
                onClick = { onSelect(status) },
            )
        }
    }
}

/**
 * 필터 칩 하나 — 이름 + 숫자. 고르면 브랜드 색으로 찬다.
 *
 * 수업 탭 필터도 같은 칩을 쓴다. 이름을 `CountChip` 으로 둔 것은 회원 전용이 아니어서고,
 * 머티리얼의 `FilterChip` 과 이름이 겹치지 않게 하려는 것도 있다.
 */
@Composable
fun CountChip(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    val background by animateColorAsState(
        targetValue = if (selected) TeamFisColor.Brand else TeamFisColor.Surface1,
        animationSpec = TeamFisMotion.fast(),
        label = "chipBackground",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) TeamFisColor.TextPrimary else TeamFisColor.TextSecondary,
        animationSpec = TeamFisMotion.fast(),
        label = "chipLabel",
    )
    val countColor by animateColorAsState(
        // 고른 칩은 브랜드 위라 흐린 회색이 안 보인다. 흰색을 반투명하게 깐다
        targetValue = if (selected) Color.White.copy(alpha = 0.7f) else TeamFisColor.TextTertiary,
        animationSpec = TeamFisMotion.fast(),
        label = "chipCount",
    )

    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .height(TeamFisSize.chip)
            .clip(TeamFisRadius.card)
            .background(background)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = TeamFisSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.xs),
    ) {
        Text(label, style = TeamFisType.bodySm, color = labelColor)
        Text(
            "$count",
            style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
            color = countColor,
        )
    }
}

/**
 * 방문 경로 — **어떻게 알고 왔나.** 신규 등록에만 받는다.
 *
 * 재등록은 처음 올 때 이미 정해진 값이라 다시 안 묻는다.
 *
 * `지인소개` 와 `개인영업` 은 **다르다** — 앞은 기존 회원이 데려온 것이라
 * `소개한 회원` 칸이 차고, 뒤는 트레이너가 직접 딴 것이라 안 찬다.
 */
enum class VisitPath(val label: String) {
    WalkIn("워크인"),
    Referral("지인소개"),
    Sales("개인영업"),
    Blog("블로그"),
    Instagram("인스타"),
    OtToPt("OT → PT"),
}

/**
 * 회원 한 줄 — 이름·상태 배지 / 아래 한 줄, 오른쪽 끝에 회차.
 *
 * **면을 안 깔고 줄만 나눈다.** 보유 회원이 수십 명이라 카드로 쌓으면 화면이 무겁고
 * 훑어 내려가기도 어렵다.
 */
@Composable
fun MemberRow(member: Member, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(
                horizontal = TeamFisSpacing.screenHorizontal,
                vertical = TeamFisSpacing.lg,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.xs)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
            ) {
                Text(
                    "${member.name} 회원님",
                    style = TeamFisType.titleSm,
                    color = TeamFisColor.TextPrimary,
                )
                StatusBadge(member.status)
            }
            Text(member.detail, style = TeamFisType.caption, color = TeamFisColor.TextTertiary)
        }

        Spacer(Modifier.weight(1f))

        Text(
            member.progress,
            // 회차는 자릿수가 바뀌어도 오른쪽 끝이 안 흔들려야 한다
            style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
            color = TeamFisColor.TextSecondary,
        )
    }
}

/**
 * 상태 배지 — **활성에는 안 붙는다.**
 *
 * 대부분이 활성이라 전부 붙이면 목록이 배지로 뒤덮인다. 배지는 눈에 걸려야 할
 * 예외(홀딩·만료)에만 붙인다.
 */
@Composable
private fun StatusBadge(status: MemberStatus) {
    if (status == MemberStatus.Active) return

    Text(
        status.label,
        style = TeamFisType.caption,
        // 만료는 재등록을 붙여야 할 자리라 브랜드 색으로 눈에 걸리게 한다
        color = if (status == MemberStatus.Expired) TeamFisColor.Brand else TeamFisColor.TextTertiary,
        modifier = Modifier
            .background(TeamFisColor.Surface2, TeamFisRadius.card)
            .padding(horizontal = TeamFisSpacing.sm, vertical = 2.dp),
    )
}

/** 줄 사이 얇은 선. */
@Composable
fun MemberDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(horizontal = TeamFisSpacing.screenHorizontal)
            .fillMaxWidth()
            .height(1.dp)
            .background(TeamFisColor.Divider),
    )
}

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 회원 목록을 주면 통째로 걷어낸다.
 * 이름은 아직 다 `000` 이다.
 */
val placeholderMembers = listOf(
    Member("000", MemberStatus.Active, "12/30회차", "마지막 9/5"),
    Member("000", MemberStatus.Active, "3/20회차", "마지막 9/6"),
    Member("000", MemberStatus.Active, "8/10회차", "마지막 9/4"),
    Member("000", MemberStatus.Active, "27/30회차", "마지막 9/6"),
    Member("000", MemberStatus.Holding, "14/40회차", "9/1부터 홀딩"),
    Member("000", MemberStatus.Active, "1/50회차", "마지막 9/2"),
    Member("000", MemberStatus.Expired, "20/20회차", "8/28 만료"),
    Member("000", MemberStatus.Active, "19/30회차", "마지막 9/3"),
    Member("000", MemberStatus.Holding, "6/20회차", "8/20부터 홀딩"),
    Member("000", MemberStatus.Expired, "30/30회차", "8/11 만료"),
    Member("000", MemberStatus.Active, "5/10회차", "마지막 9/6"),
    Member("000", MemberStatus.Expired, "10/10회차", "7/30 만료"),
)
