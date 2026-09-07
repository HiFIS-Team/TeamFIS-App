package com.teamfis.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisMotion
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * 홈 캘린더 — 헤더 바로 밑.
 *
 * 평소에는 **이번 주 한 줄**, `펼쳐보기` 를 누르면 **그 달 전체**로 늘어난다.
 * 선택은 하단 탭과 다른 규칙이다 — **브랜드 레드를 쓰지 않는다.**
 * 상시 떠 있는 것에 액센트 예산을 쓰지 않는다.
 */
@Composable
fun HomeCalendar(
    selected: LocalDate,
    /** 펼쳤을 때 보이는 달 (그 달의 아무 날) */
    month: LocalDate,
    expanded: Boolean,
    onSelect: (LocalDate) -> Unit,
    onMonthChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 높이만 줄이면 **내용이 먼저 사라지고 빈칸이 뒤늦게 닫힌다** — 아래 줄이 늦게 따라오는 것처럼 보인다.
    // 접힐 때도 내용과 높이가 같이 움직이도록 `AnimatedVisibility` 로 감싼다.
    Column(modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = !expanded,
            enter = expandVertically(TeamFisMotion.slow()) + fadeIn(TeamFisMotion.slow()),
            exit = shrinkVertically(TeamFisMotion.slow()) + fadeOut(TeamFisMotion.slow()),
        ) {
            WeekStrip(week = weekOf(selected), selected = selected, onSelect = onSelect)
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(TeamFisMotion.slow()) + fadeIn(TeamFisMotion.slow()),
            exit = shrinkVertically(TeamFisMotion.slow()) + fadeOut(TeamFisMotion.slow()),
        ) {
            Column {
                MonthHeader(
                    month = month,
                    onPrev = { onMonthChange(month.minusMonths(1)) },
                    onNext = { onMonthChange(month.plusMonths(1)) },
                )
                WeekdayHeader()
                monthWeeks(month).forEach { week ->
                    MonthRow(week = week, selected = selected, onSelect = onSelect)
                }
            }
        }
    }
}

/** 접힌 상태 — 요일과 날짜가 한 칸에 있고, 고른 칸만 알약이 채워진다 */
@Composable
private fun WeekStrip(week: List<LocalDate>, selected: LocalDate, onSelect: (LocalDate) -> Unit) {
    val index = week.indexOf(selected).coerceAtLeast(0)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.screenHorizontal),
    ) {
        val column = maxWidth / DAYS
        // 알약은 칸을 따라 **흐른다.** 칸마다 배경을 껐다 켜면 선택이 순간이동해 보인다
        val pillX by animateDpAsState(
            targetValue = column * index + (column - PillWidth) / 2,
            animationSpec = TeamFisMotion.base(),
            label = "pillX",
        )

        Box(
            Modifier
                .offset(x = pillX)
                .size(width = PillWidth, height = PillHeight)
                .background(TeamFisColor.Surface2, TeamFisRadius.full),
        )

        Row(Modifier.fillMaxWidth()) {
            week.forEach { day ->
                DayCell(
                    day = day,
                    selected = day == selected,
                    onClick = { onSelect(day) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** 펼친 상태의 달 머리글 — `2026년 9월` 과 앞뒤 달로 가는 화살표 */
@Composable
private fun MonthHeader(month: LocalDate, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(
                start = TeamFisSpacing.screenHorizontal,
                end = TeamFisSpacing.screenHorizontal - TeamFisSpacing.sm,
                bottom = TeamFisSpacing.md,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${month.year}년 ${month.monthValue}월",
            style = TeamFisType.titleMd.copy(fontFeatureSettings = "tnum"),
            color = TeamFisColor.TextPrimary,
        )
        Spacer(Modifier.weight(1f))
        // 화살표는 아래쪽 화살표 한 벌을 돌려 쓴다
        MonthArrow(rotation = 90f, description = "이전 달", onClick = onPrev)
        MonthArrow(rotation = -90f, description = "다음 달", onClick = onNext)
    }
}

@Composable
private fun MonthArrow(rotation: Float, description: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(40.dp)
            .clip(TeamFisRadius.full)
            .tapNoRipple(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = description,
            tint = TeamFisColor.TextSecondary,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer { rotationZ = rotation },
        )
    }
}

/**
 * 토요일 파랑 · 일요일 빨강 — 한국 달력 관행.
 * 고른 날은 흰 알약/원이 더 센 신호라 그쪽을 따른다.
 */
private val DayOfWeek.weekendColor: Color?
    get() = when (this) {
        DayOfWeek.SATURDAY -> TeamFisColor.WeekendSaturday
        DayOfWeek.SUNDAY -> TeamFisColor.WeekendSunday
        else -> null
    }

/** 펼친 상태의 요일 머리글 — 칸마다 요일을 반복하면 달력이 시끄럽다 */
@Composable
private fun WeekdayHeader() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.screenHorizontal),
    ) {
        weekdayOrder.forEach { day ->
            Text(
                day.koLabel,
                style = TeamFisType.caption,
                color = day.weekendColor ?: TeamFisColor.TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = TeamFisSpacing.xs),
            )
        }
    }
}

/** 펼친 상태의 한 주 — 날짜만 있고, 고른 날은 밑에 점이 붙는다 */
@Composable
private fun MonthRow(week: List<LocalDate?>, selected: LocalDate, onSelect: (LocalDate) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.screenHorizontal),
    ) {
        week.forEach { day ->
            Box(Modifier.weight(1f).height(50.dp), contentAlignment = Alignment.Center) {
                if (day != null) MonthDay(day, day == selected) { onSelect(day) }
            }
        }
    }
}

@Composable
private fun MonthDay(day: LocalDate, selected: Boolean, onClick: () -> Unit) {
    val dateFg by animateColorAsState(
        if (selected) TeamFisColor.TextPrimary else day.dayOfWeek.weekendColor ?: TeamFisColor.TextSecondary,
        TeamFisMotion.base(),
        label = "monthFg",
    )

    Column(
        modifier = Modifier.tapNoRipple(onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(StampSize), contentAlignment = Alignment.Center) {
            Text(
                day.dayOfMonth.toString(),
                style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
                color = dateFg,
            )
        }
        // 고른 날은 **동그라미 대신 밑에 점**이다 — 달력이 조용해진다
        Box(
            Modifier
                .size(DotSize)
                .background(if (selected) TeamFisColor.TextPrimary else Color.Transparent, CircleShape),
        )
    }
}

@Composable
private fun DayCell(
    day: LocalDate,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motion = TeamFisMotion.base<Color>()
    val markBg by animateColorAsState(
        if (selected) TeamFisColor.TextPrimary else Color.Transparent, motion, label = "markBg",
    )
    val weekend = day.dayOfWeek.weekendColor
    val markFg by animateColorAsState(
        if (selected) TeamFisColor.Background else weekend ?: TeamFisColor.TextTertiary, motion, label = "markFg",
    )
    val dateFg by animateColorAsState(
        if (selected) TeamFisColor.TextPrimary else weekend ?: TeamFisColor.TextSecondary, motion, label = "dateFg",
    )

    Column(
        modifier = modifier
            .height(PillHeight)
            .tapNoRipple(onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(MarkSize).background(markBg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(day.dayOfWeek.koLabel, style = TeamFisType.caption, color = markFg)
        }
        Box(
            Modifier.padding(top = TeamFisSpacing.xs).size(StampSize),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                // 날짜는 자릿수가 바뀌어도 칸 안에서 흔들리면 안 된다 (tabular)
                style = TeamFisType.titleSm.copy(fontFeatureSettings = "tnum"),
                color = dateFg,
            )
        }
    }
}

/** 달력 칸은 **물결(ripple)을 쓰지 않는다** — 칸마다 파문이 퍼지면 시끄럽다 */
@Composable
private fun Modifier.tapNoRipple(onClick: () -> Unit): Modifier {
    val interaction = remember { MutableInteractionSource() }
    return clickable(interactionSource = interaction, indication = null, onClick = onClick)
}

/** [date] 가 속한 주를 **일요일부터** 7일 반환한다 (한국 달력 관행). */
fun weekOf(date: LocalDate): List<LocalDate> {
    val sunday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    return (0 until DAYS).map { sunday.plusDays(it.toLong()) }
}

/**
 * [date] 가 속한 **달**을 주 단위로 자른다. 앞뒤 빈 칸은 `null` 이다.
 *
 * 옆 달 날짜를 흐리게 채우지 않는다 — 이 달 안에서만 고르게 한다.
 */
fun monthWeeks(date: LocalDate): List<List<LocalDate?>> {
    val first = date.withDayOfMonth(1)
    // 일요일이 첫 칸이다. `DayOfWeek.SUNDAY.value` 는 7 이라 그대로 나머지 연산에 쓴다
    val lead = first.dayOfWeek.value % DAYS
    val days: List<LocalDate?> = List(lead) { null } +
        (1..date.lengthOfMonth()).map { first.withDayOfMonth(it) }
    val padded = days + List((DAYS - days.size % DAYS) % DAYS) { null }
    return padded.chunked(DAYS)
}

private const val DAYS = 7

/** 칸 하나가 터치 타겟보다 커야 하므로 알약 높이가 곧 행 높이다 */
private val PillHeight = 68.dp
private val PillWidth = 44.dp
private val MarkSize = 26.dp

/** 날짜 칸 크기 */
private val StampSize = 40.dp

/** 고른 날 표시 — 날짜 밑 점 */
private val DotSize = 5.dp

private val weekdayOrder = listOf(
    DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
)

/**
 * 기기 로케일을 따르지 않고 우리가 정한다 — 한국어 전용 앱이고,
 * 요일 한 글자는 폭이 일정해야 칸이 흔들리지 않는다.
 */
private val DayOfWeek.koLabel: String
    get() = when (this) {
        DayOfWeek.MONDAY -> "월"
        DayOfWeek.TUESDAY -> "화"
        DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY -> "목"
        DayOfWeek.FRIDAY -> "금"
        DayOfWeek.SATURDAY -> "토"
        DayOfWeek.SUNDAY -> "일"
    }
