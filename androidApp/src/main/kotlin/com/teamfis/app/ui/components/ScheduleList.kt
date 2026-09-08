package com.teamfis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/** 일정에 서는 수업 한 건. */
data class ScheduleClass(
    val member: String,
    /** `오후 2:00 ~ 3:00` */
    val time: String,
    /** 등록 상품 — `얼리버드 20회` */
    val product: String,
    /** `12/30회차` */
    val progress: String,
    val status: SessionStatus,
)

/**
 * 일정의 수업 카드.
 *
 * **시간이 제일 크다.** 일정 화면에서 찾는 것은 "누가"보다 "몇 시에"다 —
 * 하루를 시간 순으로 훑는 자리라 시간이 눈에 먼저 걸려야 한다.
 * (회원 상세의 회차 카드는 반대로 회차가 크다. 거기선 순서가 먼저다)
 *
 * 왼쪽 점은 **상태를 색으로만** 말한다. 배지 글자를 읽지 않고도 세로로 훑을 수 있다.
 */
@Composable
fun ScheduleClassCard(
    item: ScheduleClass,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(TeamFisRadius.card)
            .background(TeamFisColor.Surface1)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(TeamFisSpacing.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(DotSize)
                    .background(item.status.dotColor, CircleShape),
            )
            Text(
                "${item.member} 회원님",
                style = TeamFisType.bodySm,
                color = TeamFisColor.TextSecondary,
                modifier = Modifier.padding(start = TeamFisSpacing.sm),
            )

            Spacer(Modifier.weight(1f))

            SessionBadge(item.status)
        }

        Text(
            item.time,
            // 시간은 자릿수가 바뀌어도 줄이 안 흔들려야 한다
            style = TeamFisType.titleLg.copy(fontFeatureSettings = "tnum"),
            color = TeamFisColor.TextPrimary,
            modifier = Modifier.padding(top = TeamFisSpacing.sm),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TeamFisSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
        ) {
            Text(item.product, style = TeamFisType.caption, color = TeamFisColor.TextTertiary)
            Spacer(Modifier.weight(1f))
            Text(
                item.progress,
                style = TeamFisType.caption.copy(fontFeatureSettings = "tnum"),
                color = TeamFisColor.TextTertiary,
            )
        }
    }
}

private val DotSize = 8.dp
