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
import java.time.LocalDateTime

/**
 * 일정에 서는 수업 한 건.
 *
 * **시각을 글자가 아니라 값으로 들고 있다** — 상세에서 날짜·시간을 고칠 수 있어야 해서다.
 * 카드가 읽는 [time] 은 그 값에서 나온다.
 */
data class ScheduleClass(
    val member: String,
    /** 수업 시작 시각 */
    val at: LocalDateTime,
    /** 수업 길이(분) — 시작을 옮기면 끝도 따라 움직인다 */
    val minutes: Int,
    /** 등록 상품 — `얼리버드 20회` */
    val product: String,
    /** `12/30회차` */
    val progress: String,
    val status: SessionStatus,
) {
    /** `오후 2:00 ~ 3:00` */
    val time: String get() = timeRange(at, minutes)
}

/**
 * `오후 2:00 ~ 3:00`.
 *
 * 끝 시각에는 오전·오후를 **넘어갈 때만** 붙인다. 한 줄 안에서 같은 말을 두 번 하면
 * 정작 다른 쪽인 시각이 안 보인다.
 */
fun timeRange(at: LocalDateTime, minutes: Int): String {
    val end = at.plusMinutes(minutes.toLong())
    val sameHalf = (at.hour < 12) == (end.hour < 12)
    return "${ampmTime(at)} ~ ${if (sameHalf) clockTime(end) else ampmTime(end)}"
}

/** `오후 2:00` */
fun ampmTime(at: LocalDateTime): String =
    "${if (at.hour < 12) "오전" else "오후"} ${clockTime(at)}"

/** `2:00` — 12시간제. 0 시와 12 시는 둘 다 `12` 다 */
fun clockTime(at: LocalDateTime): String {
    val hour = at.hour % 12
    return "${if (hour == 0) 12 else hour}:%02d".format(at.minute)
}

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
    /**
     * 큰 시각 위에 붙는 날짜 한 줄. **여러 날이 섞이는 목록에서만 넘긴다.**
     * 일정은 하루치만 세우므로 안 넘기고, 그래서 일정 카드는 예전 그대로다
     */
    date: String? = null,
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

        if (date != null) {
            Text(
                date,
                style = TeamFisType.caption,
                color = TeamFisColor.TextTertiary,
                modifier = Modifier.padding(top = TeamFisSpacing.sm),
            )
        }

        Text(
            item.time,
            // 시간은 자릿수가 바뀌어도 줄이 안 흔들려야 한다
            style = TeamFisType.titleLg.copy(fontFeatureSettings = "tnum"),
            color = TeamFisColor.TextPrimary,
            // 날짜가 붙으면 둘이 한 덩어리라 사이를 좁힌다
            modifier = Modifier.padding(
                top = if (date == null) TeamFisSpacing.sm else TeamFisSpacing.xs,
            ),
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
