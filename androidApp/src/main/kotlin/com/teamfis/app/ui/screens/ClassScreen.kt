package com.teamfis.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teamfis.app.ui.components.ClassFilter
import com.teamfis.app.ui.components.ClassFilterBar
import com.teamfis.app.ui.components.ScheduleClass
import com.teamfis.app.ui.components.ScheduleClassCard
import com.teamfis.app.ui.components.SessionStatus
import com.teamfis.app.ui.components.dayTitle
import com.teamfis.app.ui.shell.AppHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.LocalDate

/**
 * 수업.
 *
 * **끝난 수업 중 아직 안 닫힌 것을 모아 두는 자리다.** 일지를 안 썼거나 사인을
 * 못 받았거나 — 트레이너가 수업 뒤에 할 일은 그 둘뿐이라 필터도 둘이다.
 *
 * 카드는 **일정과 같은 것**을 쓴다 (2026-09-08 대표 지시). 같은 수업 한 건이
 * 화면마다 다르게 생기면 안 된다. 다만 여기는 여러 날이 섞이므로 **날짜 한 줄이 더 붙는다** —
 * 일정은 하루치만 세워 날짜가 필요 없었다.
 *
 * 목록이 길어지므로 필터 줄은 **위에 고정**한다 (회원 목록과 같은 이유).
 *
 * 카드를 누르면 수업 상세로 간다 — 일지와 사인이 거기 있다.
 */
@Composable
fun ClassScreen(
    onClass: (ScheduleClass) -> Unit = {},
    onNotification: () -> Unit = {},
) {
    var filter by rememberSaveable { mutableStateOf(ClassFilter.Log) }

    val counts = remember { ClassFilter.entries.associateWith { pending(it).size } }
    val shown = remember(filter) { pending(filter) }

    Column(Modifier.fillMaxSize()) {
        AppHeader(onNotification = onNotification)

        ClassFilterBar(
            selected = filter,
            counts = counts,
            onSelect = { filter = it },
            modifier = Modifier.padding(
                top = TeamFisSpacing.sm,
                bottom = TeamFisSpacing.md,
            ),
        )

        if (shown.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("밀린 수업이 없어요", style = TeamFisType.bodySm, color = TeamFisColor.TextMuted)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = TeamFisSpacing.screenHorizontal,
                    end = TeamFisSpacing.screenHorizontal,
                    bottom = TeamFisSpacing.xxxl,
                ),
                verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                items(shown) { item ->
                    ScheduleClassCard(
                        item,
                        onClick = { onClass(item) },
                        date = dayTitle(item.at.toLocalDate()),
                    )
                }
            }
        }
    }
}

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 밀린 수업을 주면 통째로 걷어낸다.
 *
 * 둘 다 **끝난 수업**이다 — 예정은 아직 쓸 것이 없고 노쇼는 한 게 없다.
 */
private fun pending(filter: ClassFilter): List<ScheduleClass> {
    val today = LocalDate.now()
    return when (filter) {
        ClassFilter.Log -> listOf(
            ScheduleClass(
                "000", today.atTime(10, 0), 60,
                "얼리버드 20회", "12/20회차", SessionStatus.Done,
            ),
            ScheduleClass(
                "000", today.minusDays(1).atTime(19, 30), 60,
                "PT 30회", "12/30회차", SessionStatus.Done,
            ),
            ScheduleClass(
                "000", today.minusDays(2).atTime(14, 0), 60,
                "PT 20회", "3/20회차", SessionStatus.Done,
            ),
        )

        ClassFilter.Sign -> listOf(
            ScheduleClass(
                "000", today.minusDays(1).atTime(11, 0), 60,
                "얼리버드 10회", "8/10회차", SessionStatus.Done,
            ),
            ScheduleClass(
                "000", today.minusDays(3).atTime(20, 0), 60,
                "PT 30회", "24/30회차", SessionStatus.Done,
            ),
        )
    }
}
