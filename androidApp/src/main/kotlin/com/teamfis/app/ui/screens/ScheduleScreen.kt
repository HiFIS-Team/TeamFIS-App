package com.teamfis.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.components.ScheduleCalendar
import com.teamfis.app.ui.components.ScheduleClass
import com.teamfis.app.ui.components.ScheduleClassCard
import com.teamfis.app.ui.components.SessionStatus
import com.teamfis.app.ui.shell.AppHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisMotion
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSize
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 일정.
 *
 * 달력은 원래 홈에 있었는데 **일정으로 옮겼다** (2026-09-08 대표 지시).
 * 홈은 다른 화면이 다 찬 뒤에 마지막으로 짠다.
 *
 * **헤더만 고정이고 날짜 제목부터 아래는 전부 스크롤한다.**
 *
 * 짜임새는 팀버핏 코치 앱을 참고했다 (2026-09-08 대표 지시) —
 * 큰 날짜 제목 → 주 달력 → 그날 수업 카드. 값은 TeamFIS 것(PT 1:1)으로 갈았다.
 */
@Composable
fun ScheduleScreen(
    onClass: (ScheduleClass) -> Unit = {},
    onNotification: () -> Unit = {},
    onMy: () -> Unit = {},
) {
    var selected by remember { mutableStateOf(java.time.LocalDate.now()) }
    var month by remember { mutableStateOf(java.time.LocalDate.now()) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        AppHeader(onNotification = onNotification, onMy = onMy)

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = TeamFisSpacing.xxxl),
        ) {
            ScheduleCalendar(
                selected = selected,
                month = month,
                expanded = expanded,
                onSelect = {
                    selected = it
                    month = it
                },
                onMonthChange = { month = it },
                modifier = Modifier.padding(top = TeamFisSpacing.sm),
                hasClass = ::hasClass,
            )
            CalendarBar(
                expanded = expanded,
                onToggle = { expanded = !expanded },
                modifier = Modifier.padding(top = TeamFisSpacing.xs),
            )

            val classes = remember(selected) { classesOn(selected) }
            if (classes.isEmpty()) {
                Text(
                    "일정이 없어요",
                    style = TeamFisType.bodySm,
                    color = TeamFisColor.TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = TeamFisSpacing.xxxl),
                )
            } else {
                Column(
                    modifier = Modifier.padding(
                        top = TeamFisSpacing.lg,
                        start = TeamFisSpacing.screenHorizontal,
                        end = TeamFisSpacing.screenHorizontal,
                    ),
                    verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
                ) {
                    classes.forEach { item ->
                        ScheduleClassCard(item, onClick = { onClass(item) })
                    }
                }
            }
        }
    }
}

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 일정을 주면 통째로 걷어낸다.
 * 오늘·내일·사흘 뒤에만 수업이 있는 것으로 둔다 — 점이 찍히는 날과 목록이 어긋나면 안 된다.
 */
private fun hasClass(date: java.time.LocalDate): Boolean {
    val today = java.time.LocalDate.now()
    return date == today || date == today.plusDays(1) || date == today.plusDays(3)
}

private fun classesOn(date: java.time.LocalDate): List<ScheduleClass> =
    if (!hasClass(date)) emptyList() else listOf(
        ScheduleClass("000", date.atTime(10, 0), 60, "얼리버드 20회", "12/20회차", SessionStatus.Done),
        ScheduleClass("000", date.atTime(14, 0), 60, "PT 30회", "12/30회차", SessionStatus.Scheduled),
        ScheduleClass("000", date.atTime(16, 0), 60, "PT 20회", "3/20회차", SessionStatus.Scheduled),
        ScheduleClass("000", date.atTime(18, 30), 60, "얼리버드 10회", "8/10회차", SessionStatus.Scheduled),
    )

/**
 * 캘린더 아래 한 줄 — `펼쳐보기`.
 *
 * 펼치기는 **화살표가 뒤집히며** 캘린더가 그 달로 늘어난다.
 */
@Composable
private fun CalendarBar(expanded: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val arrow by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = TeamFisMotion.base(),
        label = "arrow",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.screenHorizontal - TeamFisSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .height(TeamFisSize.chip)
                .clip(TeamFisRadius.full)
                .clickable(interactionSource = interaction, indication = null, onClick = onToggle)
                .padding(horizontal = TeamFisSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                if (expanded) "접기" else "펼쳐보기",
                style = TeamFisType.bodySm,
                color = TeamFisColor.TextSecondary,
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = null, // 옆 글자가 이름 역할을 한다
                tint = TeamFisColor.TextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .graphicsLayer { rotationZ = arrow },
            )
        }
    }
}
