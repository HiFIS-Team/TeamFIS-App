package com.teamfis.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.components.HomeCalendar
import com.teamfis.app.ui.components.NextClassBanner
import com.teamfis.app.ui.components.TodayClass
import com.teamfis.app.ui.components.TodayClassCard
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisMotion
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSize
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.LocalDate

/** 홈 — 헤더 밑에 캘린더. 아래 내용은 아직 없다. */
@Composable
fun HomeScreen() {
    var selected by remember { mutableStateOf(LocalDate.now()) }
    var month by remember { mutableStateOf(LocalDate.now()) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        HomeCalendar(
            selected = selected,
            month = month,
            expanded = expanded,
            onSelect = {
                selected = it
                month = it
            },
            onMonthChange = { month = it },
        )
        CalendarBar(expanded = expanded, onToggle = { expanded = !expanded })

        Spacer(Modifier.height(TeamFisSpacing.md))
        // 값은 아직 자리 표시자다 (데이터가 붙으면 갈아끼운다)
        NextClassBanner(member = "000", time = "오후 2:00")

        TodayClasses()
    }
}

/** 오늘 수업 — **시간 순**으로 앞의 세 건만 보여준다. */
@Composable
private fun TodayClasses() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = TeamFisSpacing.lg, bottom = TeamFisSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
    ) {
        Text(
            "오늘 수업",
            style = TeamFisType.titleMd,
            color = TeamFisColor.TextPrimary,
            modifier = Modifier.padding(horizontal = TeamFisSpacing.screenHorizontal),
        )
        placeholderClasses.forEach { item ->
            TodayClassCard(
                item = item,
                modifier = Modifier.padding(horizontal = TeamFisSpacing.screenHorizontal),
            )
        }
    }
}

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 오늘 수업을 주면 통째로 걷어낸다.
 * 시간 순으로 이미 정렬돼 있다.
 */
private val placeholderClasses = listOf(
    TodayClass(member = "000", time = "오후 2:00", detail = "PT 12/30회차"),
    TodayClass(member = "000", time = "오후 4:00", detail = "PT 3/20회차"),
    TodayClass(member = "000", time = "오후 6:30", detail = "PT 8/10회차"),
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
