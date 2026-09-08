package com.teamfis.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 수업이 끝난 뒤에 쓴 일지.
 *
 * **수업 상세에서는 읽기만 한다.** 쓰는 자리는 수업 탭이다 — 한 자리에서 쓰고
 * 여러 자리에서 보는 게 맞고, 두 군데서 쓰게 하면 어느 쪽이 최신인지 알 수 없다.
 */
data class ScheduleLog(
    val parts: List<BodyPart>,
    val note: String,
)

/**
 * 일지 조회 블록 — 운동 부위 칩 + 메모.
 *
 * 회원 상세의 회차 카드가 부위만 보여 주는 것과 달리 **메모까지 편다.**
 * 거기는 회차를 훑는 목록이고 여기는 수업 하나만 있는 화면이라 접을 이유가 없다.
 */
@Composable
fun ScheduleLogBlock(log: ScheduleLog, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Text("일지", style = TeamFisType.bodySm, color = TeamFisColor.TextSecondary)

        if (log.parts.isNotEmpty()) {
            BodyPartChips(log.parts, modifier = Modifier.padding(top = TeamFisSpacing.md))
        }

        if (log.note.isNotBlank()) {
            Text(
                log.note,
                style = TeamFisType.bodySm,
                color = TeamFisColor.TextSecondary,
                modifier = Modifier.padding(top = TeamFisSpacing.md),
            )
        }
    }
}

/** `3월 21일 (토)` — 수업 상세의 머리에 선다. */
fun dayTitle(date: LocalDate): String =
    "${date.monthValue}월 ${date.dayOfMonth}일 (${date.dayOfWeek.koLabel})"

/** `2026. 3. 21. (토)` — 회원 상세의 날짜 표기에 요일만 더했다. 무슨 요일 수업인지가 중요해서다. */
fun dayLabel(date: LocalDate): String =
    "${date.year}. ${date.monthValue}. ${date.dayOfMonth}. (${date.dayOfWeek.koLabel})"

/** 날짜만 갈아 끼운다 — 시각은 그대로 둔다. */
fun LocalDateTime.onDate(date: LocalDate): LocalDateTime =
    LocalDateTime.of(date, toLocalTime())
