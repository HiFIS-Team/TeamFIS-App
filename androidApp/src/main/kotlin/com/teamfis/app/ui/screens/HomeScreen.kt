package com.teamfis.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.components.ActionTiles
import com.teamfis.app.ui.components.CareMember
import com.teamfis.app.ui.components.CareMemberRow
import com.teamfis.app.ui.components.HomeCalendar
import com.teamfis.app.ui.components.MonthStat
import com.teamfis.app.ui.components.MonthSummary
import com.teamfis.app.ui.components.NextClassBanner
import com.teamfis.app.ui.components.PendingLog
import com.teamfis.app.ui.components.PendingLogList
import com.teamfis.app.ui.components.SectionHeader
import com.teamfis.app.ui.components.TodayClass
import com.teamfis.app.ui.components.TodayClassCard
import com.teamfis.app.ui.shell.AppHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisMotion
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSize
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.LocalDate

/**
 * 홈.
 *
 * 섹션 순서는 **놓치면 손해 보는 순서**다 — 오늘 할 일(오늘 수업) → 챙길 사람(챙길 회원)
 * → 밀린 일(미작성 일지) → 돌아보는 숫자(이번 달).
 *
 * 섹션마다 **아래 내용의 생김새가 다르다.** 같은 카드가 끝까지 내려오면 화면이 지루하고,
 * 어디가 어디인지 훑어서 못 찾는다. 제목 줄만 [SectionHeader] 로 통일한다.
 */
@Composable
fun HomeScreen() {
    var selected by remember { mutableStateOf(LocalDate.now()) }
    var month by remember { mutableStateOf(LocalDate.now()) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    // **헤더만 고정이고 달력부터 아래는 전부 스크롤한다** (MyFIS 홈과 같은 구조).
    // 헤더는 화면이 직접 그린다 — 셸이 고정하면 회원 상세가 헤더를 걷어낼 수 없다
    Column(Modifier.fillMaxSize()) {
        AppHeader()

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = TeamFisSpacing.xxxl),
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
                modifier = Modifier.padding(top = TeamFisSpacing.sm),
            )
            CalendarBar(
                expanded = expanded,
                onToggle = { expanded = !expanded },
                modifier = Modifier.padding(top = TeamFisSpacing.xs),
            )

            Spacer(Modifier.height(TeamFisSpacing.md))
            // 값은 아직 자리 표시자다 (데이터가 붙으면 갈아끼운다)
            NextClassBanner(member = "000", time = "오후 2:00")

            ActionTiles(
                modifier = Modifier.padding(
                    top = TeamFisSpacing.lg,
                    start = TeamFisSpacing.screenHorizontal,
                    end = TeamFisSpacing.screenHorizontal,
                ),
            )

            // 오늘 수업 — 큰 카드가 세로로 쌓인다
            Section(
                title = "오늘 수업",
                actionLabel = "전체보기",
                // 전체 목록 화면이 생기면 연결한다
                onAction = {},
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md)) {
                    placeholderClasses.forEach { item ->
                        TodayClassCard(
                            item = item,
                            modifier = Modifier.padding(horizontal = TeamFisSpacing.screenHorizontal),
                        )
                    }
                }
            }

            // 챙길 회원 — 좁은 카드를 가로로 넘긴다
            Section(title = "챙길 회원") {
                CareMemberRow(placeholderCare)
            }

            // 미작성 일지 — 면 없이 줄만 나눈다
            Section(title = "미작성 일지", count = placeholderLogs.size) {
                PendingLogList(placeholderLogs)
            }

            // 이번 달 — 판 하나에 숫자 셋
            Section(title = "이번 달") {
                MonthSummary(placeholderMonth)
            }
        }
    }
}

/** 섹션 하나 — 제목 줄 + 내용. 섹션 사이 간격을 한 곳에서 정한다. */
@Composable
private fun Section(
    title: String,
    count: Int? = null,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = TeamFisSpacing.xxxl),
        verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
    ) {
        SectionHeader(title = title, count = count, actionLabel = actionLabel, onAction = onAction)
        content()
    }
}

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 값을 주면 통째로 걷어낸다.
 * 오늘 수업은 시간 순으로 이미 정렬돼 있다.
 */
private val placeholderClasses = listOf(
    TodayClass(member = "000", time = "오후 2:00", detail = "PT 12/30회차"),
    TodayClass(member = "000", time = "오후 4:00", detail = "PT 3/20회차"),
    TodayClass(member = "000", time = "오후 6:30", detail = "PT 8/10회차"),
)

private val placeholderCare = listOf(
    CareMember(name = "000", reason = "2회 남음"),
    CareMember(name = "000", reason = "3회 남음"),
    CareMember(name = "000", reason = "12일 안 옴"),
    CareMember(name = "000", reason = "21일 안 옴"),
)

private val placeholderLogs = listOf(
    PendingLog(date = "9/6 (토)", member = "000"),
    PendingLog(date = "9/5 (금)", member = "000"),
    PendingLog(date = "9/5 (금)", member = "000"),
)

private val placeholderMonth = listOf(
    MonthStat(value = "48", label = "세션"),
    MonthStat(value = "3", label = "신규"),
    MonthStat(value = "5", label = "재등록"),
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
