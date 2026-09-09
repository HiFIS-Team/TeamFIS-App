package com.teamfis.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/** 마이 화면이 보여 주는 값 한 벌 — 나에 대한 것만 든다. */
data class TrainerProfile(
    val name: String,
    val branch: String,
    val phone: String,
    /** 이번 달 신규 등록 건수 */
    val newMembers: Int,
    /** 이번 달 재등록 건수 */
    val renewals: Int,
    /** 이번 달 매출 (원) */
    val revenue: Int,
    /**
     * 이번 달 내 몫 (원).
     *
     * **앱이 요율을 모른다.** 등록권마다 결제액은 들고 있지만 요율은 지점이 정하는
     * 값이라, 이 숫자는 서버가 계산해서 준다.
     */
    val incentive: Int,
    /** 이번 달 진행한 회차 */
    val doneRounds: Int,
    /** 이번 달 노쇼 회차 */
    val noShowRounds: Int,
)

/**
 * 마이의 머리 — 지점 한 줄과 **큰 인사말**.
 *
 * 대표가 준 마이페이지 짜임을 따랐다 (2026-09-09). 이 화면은 목록이 짧아서
 * 위가 비면 화면이 허전한데, **인사말이 그 자리를 채운다.** 회원 상세처럼
 * 정보를 늘어놓지 않는 것은 여기가 남이 아니라 **나**라서다 — 내 번호를 내가 볼 일은 없다.
 */
@Composable
fun TrainerHeader(profile: TrainerProfile, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Text(
            profile.branch,
            style = TeamFisType.bodySm,
            color = TeamFisColor.TextTertiary,
        )
        Text(
            "안녕하세요\n${profile.name} 트레이너님!",
            style = TeamFisType.titleLg,
            color = TeamFisColor.TextPrimary,
            modifier = Modifier.padding(top = TeamFisSpacing.md),
        )
    }
}

/**
 * 마이의 구역 머리 — `이번 달` · `수업` · `설정`.
 *
 * 회원 일지의 [MemberSectionHeader] 와 같은 값이지만 이름이 회원 것이라 여기서는
 * 안 쓴다. 둘을 하나로 합치는 것은 세 번째 화면이 같은 줄을 쓸 때 하면 된다.
 */
@Composable
fun MySectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        style = TeamFisType.titleSm,
        color = TeamFisColor.TextPrimary,
        modifier = modifier.padding(bottom = TeamFisSpacing.md),
    )
}

/**
 * 숫자 한 줄 — 이름표 왼쪽, 값 오른쪽.
 *
 * 회원 상세의 [DetailInfoRow] 와 같은 짜임이지만 **강조할 줄이 하나 있다** —
 * 내 몫이다. 트레이너가 이 화면에서 찾는 것이 그 숫자라, 나머지와 같은 무게로
 * 두면 눈이 어디에 앉을지 모른다.
 */
@Composable
fun MyStatRow(label: String, value: String, highlight: Boolean = false) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            label,
            style = TeamFisType.bodySm,
            color = if (highlight) TeamFisColor.TextSecondary else TeamFisColor.TextTertiary,
        )
        Spacer(Modifier.weight(1f))
        Text(
            value,
            // 돈·건수라 자릿수가 바뀌어도 오른쪽 끝이 안 흔들려야 한다
            style = if (highlight) {
                TeamFisType.titleSm.copy(fontFeatureSettings = "tnum")
            } else {
                TeamFisType.bodySm.copy(fontFeatureSettings = "tnum")
            },
            color = if (highlight) TeamFisColor.Brand else TeamFisColor.TextSecondary,
        )
    }
}

/** 숫자 줄들을 담는 판. */
@Composable
fun MyStatCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .padding(TeamFisSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
    ) {
        content()
    }
}

/** 내 몫 위에 긋는 얇은 선 — 위는 가게 것, 아래는 내 것이다. */
@Composable
fun MyStatDivider() {
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(TeamFisColor.Divider),
    )
}

/**
 * 메뉴 한 줄 — 아이콘 · 글자 · (값) · 화살표.
 *
 * **판을 안 깐다** (2026-09-09 대표가 준 짜임). 메뉴는 줄로 서고 갈래가 바뀌는
 * 자리에만 선을 긋는다. 줄마다 카드를 깔면 셋만 있어도 화면이 무거워진다.
 *
 * 로그아웃처럼 **되돌리기 어려운 것은 브랜드 색**으로 적어 눈에 걸리게 한다.
 */
@Composable
fun MyMenuRow(
    @DrawableRes icon: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** 오른쪽 화살표 앞에 붙는 값 — 없으면 안 그린다 */
    value: String? = null,
    danger: Boolean = false,
) {
    val interaction = remember { MutableInteractionSource() }
    val tint = if (danger) TeamFisColor.Brand else TeamFisColor.TextPrimary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(vertical = TeamFisSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null, // 옆 글자가 이름 역할을 한다
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
        Text(
            label,
            style = TeamFisType.body,
            color = tint,
            modifier = Modifier.padding(start = TeamFisSpacing.md),
        )

        Spacer(Modifier.weight(1f))

        if (value != null) {
            Text(
                value,
                style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
                color = TeamFisColor.TextTertiary,
                modifier = Modifier.padding(end = TeamFisSpacing.sm),
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = if (danger) TeamFisColor.Brand else TeamFisColor.TextTertiary,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** 메뉴 갈래를 가르는 선 — 줄과 줄 사이가 아니라 **묶음과 묶음 사이**에만 긋는다. */
@Composable
fun MyMenuDivider(modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .padding(vertical = TeamFisSpacing.sm)
            .fillMaxWidth()
            .height(1.dp)
            .background(TeamFisColor.Divider),
    )
}

/** 화면 맨 아래 한 줄 — 버전. 눌러도 아무 일 없다. */
@Composable
fun MyVersionNote(version: String, modifier: Modifier = Modifier) {
    Text(
        version,
        style = TeamFisType.caption,
        color = TeamFisColor.TextMuted,
        modifier = modifier.fillMaxWidth(),
    )
}
