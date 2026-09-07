package com.teamfis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/** 챙길 회원 한 명 — 재등록이 임박했거나 오래 안 온 회원. */
data class CareMember(val name: String, val reason: String)

/**
 * 챙길 회원 — **가로로 넘긴다.**
 *
 * 오늘 수업이 세로로 쌓이는 큰 카드라, 그 밑까지 같은 모양이면 화면이 지루하다.
 * 여기는 좁은 카드를 옆으로 흘려 **성격이 다른 목록**임을 모양으로 알린다.
 */
@Composable
fun CareMemberRow(members: List<CareMember>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = TeamFisSpacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
    ) {
        members.forEach { member ->
            Column(
                modifier = Modifier
                    .width(CareCardWidth)
                    .background(TeamFisColor.Surface1, TeamFisRadius.card)
                    .padding(TeamFisSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.xs),
            ) {
                Text(
                    "${member.name} 회원님",
                    style = TeamFisType.titleSm,
                    color = TeamFisColor.TextPrimary,
                )
                Text(
                    member.reason,
                    // 챙길 이유가 이 카드의 요점이라 여기에만 브랜드 색을 쓴다
                    style = TeamFisType.bodySm,
                    color = TeamFisColor.Brand,
                )
            }
        }
    }
}

/** 아직 안 쓴 일지 한 건. */
data class PendingLog(val date: String, val member: String)

/**
 * 미작성 일지 — **면을 안 깔고 줄만 나눈다.**
 *
 * 카드로 만들면 오늘 수업과 구분이 안 되고, 밀린 일이 여러 건일 때 화면이 무거워진다.
 * 줄 사이 얇은 선만 두어 **처리해야 할 목록**처럼 보이게 한다.
 */
@Composable
fun PendingLogList(
    logs: List<PendingLog>,
    onWrite: (PendingLog) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        logs.forEachIndexed { index, log ->
            if (index > 0) {
                Box(
                    Modifier
                        .padding(horizontal = TeamFisSpacing.screenHorizontal)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(TeamFisColor.Divider),
                )
            }

            val interaction = remember { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        onClick = { onWrite(log) },
                    )
                    .padding(
                        horizontal = TeamFisSpacing.screenHorizontal,
                        vertical = TeamFisSpacing.lg,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    log.date,
                    style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
                    color = TeamFisColor.TextTertiary,
                )
                Text(
                    "${log.member} 회원님",
                    style = TeamFisType.bodySm,
                    color = TeamFisColor.TextPrimary,
                    modifier = Modifier.padding(start = TeamFisSpacing.md),
                )

                Spacer(Modifier.weight(1f))

                Text("작성", style = TeamFisType.bodySm, color = TeamFisColor.Brand)
            }
        }
    }
}

/** 이번 달 숫자 한 칸. */
data class MonthStat(val value: String, val label: String)

/**
 * 이번 달 요약 — **판 하나에 숫자 셋.**
 *
 * 목록이 아니라 숫자라서 목록처럼 생기면 안 된다. 한 판 안에서 세로 선으로만 나눈다.
 */
@Composable
fun MonthSummary(stats: List<MonthStat>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.screenHorizontal)
            .height(IntrinsicSize.Min)
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .padding(vertical = TeamFisSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        stats.forEachIndexed { index, stat ->
            if (index > 0) {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(TeamFisColor.Divider),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    stat.value,
                    // 숫자가 이 판의 주인공이라 제일 크다
                    style = TeamFisType.titleLg.copy(fontFeatureSettings = "tnum"),
                    color = TeamFisColor.TextPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    stat.label,
                    style = TeamFisType.caption,
                    color = TeamFisColor.TextTertiary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private val CareCardWidth = 160.dp
