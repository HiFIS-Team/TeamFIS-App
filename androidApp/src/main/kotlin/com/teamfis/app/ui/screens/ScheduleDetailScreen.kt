package com.teamfis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teamfis.app.ui.components.BodyPart
import com.teamfis.app.ui.components.DetailInfoRow
import com.teamfis.app.ui.components.FieldLabel
import com.teamfis.app.ui.components.PickerField
import com.teamfis.app.ui.components.ScheduleClass
import com.teamfis.app.ui.components.ScheduleLog
import com.teamfis.app.ui.components.ScheduleLogBlock
import com.teamfis.app.ui.components.SessionBadge
import com.teamfis.app.ui.components.SessionButton
import com.teamfis.app.ui.components.SessionStatus
import com.teamfis.app.ui.components.dayLabel
import com.teamfis.app.ui.components.dayTitle
import com.teamfis.app.ui.components.onDate
import com.teamfis.app.ui.components.timeRange
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.Instant
import java.time.ZoneId

/**
 * 수업 상세 — 일정에서 수업 카드를 눌렀을 때.
 *
 * **머리에 날짜가 선다.** 회원 상세 머리가 `000 회원님` 이라 여기도 이름이면 두 화면이
 * 같은 머리를 이고 있게 된다. 이 화면의 정체는 **어느 날 몇 시 수업**이고, 이름은
 * 본문 첫 줄에서 제일 큰 글자로 받는다.
 *
 * 순서는 **누구 → 무엇 → 언제 → 처리**다. 날짜·시간만 칸에 들어 있는데,
 * **칸이 곧 고칠 수 있다는 표시**다. 나머지는 줄로만 적어 못 고치는 것과 갈라 둔다.
 *
 * 회원 상세의 회차 카드는 그대로 두었다 — 지금 수업 하나에 이르는 길은 일정뿐이다.
 *
 * **잎 화면이다** — 셸의 `NavHost` 가 오른쪽에서 밀어 넣어 하단 탭 바까지 덮는다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(item: ScheduleClass, onBack: () -> Unit) {
    // 고친 날짜·시간은 여기까지만이다. 자리 표시자라 뒤로 갔다 오면 원래대로 —
    // TODO(서버): 일정 수정 API 가 붙으면 고칠 때마다 보낸다
    var at by remember(item) { mutableStateOf(item.at) }
    var datePickerOpen by remember { mutableStateOf(false) }
    var timePickerOpen by remember { mutableStateOf(false) }

    val log = remember(item) { placeholderLog(item) }

    Column(
        Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // 머리는 고정, 아래만 흐른다 (회원 상세와 같은 모양)
        DetailHeader(title = dayTitle(at.toLocalDate()), onBack = onBack)

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = TeamFisSpacing.sm,
                        start = TeamFisSpacing.screenHorizontal,
                        end = TeamFisSpacing.screenHorizontal,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${item.member} 회원님",
                    style = TeamFisType.titleLg,
                    color = TeamFisColor.TextPrimary,
                )
                Spacer(Modifier.weight(1f))
                SessionBadge(item.status)
            }

            Column(
                modifier = Modifier.padding(
                    top = TeamFisSpacing.xl,
                    start = TeamFisSpacing.screenHorizontal,
                    end = TeamFisSpacing.screenHorizontal,
                ),
                verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                DetailInfoRow("등록권", item.product)
                DetailInfoRow("회차", item.progress)
            }

            Box(
                Modifier
                    .padding(
                        top = TeamFisSpacing.xl,
                        start = TeamFisSpacing.screenHorizontal,
                        end = TeamFisSpacing.screenHorizontal,
                    )
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TeamFisColor.Divider),
            )

            Column(
                modifier = Modifier.padding(
                    top = TeamFisSpacing.lg,
                    start = TeamFisSpacing.screenHorizontal,
                    end = TeamFisSpacing.screenHorizontal,
                ),
            ) {
                FieldLabel("날짜")
                PickerField(
                    label = "날짜",
                    value = dayLabel(at.toLocalDate()),
                    onTap = { datePickerOpen = true },
                )

                FieldLabel(
                    "시간",
                    modifier = Modifier.padding(top = TeamFisSpacing.lg),
                )
                PickerField(
                    label = "시간",
                    value = timeRange(at, item.minutes),
                    onTap = { timePickerOpen = true },
                )
            }

            // 끝난 수업에만 온다. 예정이면 아직 쓸 것이 없고, 노쇼는 한 게 없다
            log?.let {
                ScheduleLogBlock(
                    it,
                    modifier = Modifier.padding(
                        top = TeamFisSpacing.xl,
                        start = TeamFisSpacing.screenHorizontal,
                        end = TeamFisSpacing.screenHorizontal,
                    ),
                )
            }

            Spacer(Modifier.height(TeamFisSpacing.xxxl))
        }

        // **처리는 화면 아래에 붙인다** (2026-09-08 대표 지시). 흐르는 값 사이에 끼면
        // 스크롤 위치에 따라 있다 없다 하고, 아래가 통째로 비어 보인다.
        // 등록 화면의 `BottomActionButton` 과 같은 자리다
        if (item.status == SessionStatus.Scheduled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = TeamFisSpacing.screenHorizontal,
                        vertical = TeamFisSpacing.md,
                    ),
                horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                // TODO(서버): 회차 처리 API 가 붙어야 실제로 바뀐다 (회원 상세와 같다)
                SessionButton("노쇼", TeamFisColor.Surface2, TeamFisColor.TextSecondary) {}
                SessionButton("완료", TeamFisColor.Brand, TeamFisColor.TextPrimary) {}
            }
        }
    }

    if (datePickerOpen) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = at.toLocalDate()
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            // 앞날도 고를 수 있다 — 수업을 미루는 자리라 지난 날만 될 이유가 없다
            selectableDates = AnyDate,
        )
        DatePickerDialog(
            onDismissRequest = { datePickerOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { at = at.onDate(localDate(it)) }
                    datePickerOpen = false
                }) { Text("고르기", color = TeamFisColor.Brand) }
            },
            dismissButton = {
                TextButton(onClick = { datePickerOpen = false }) {
                    Text("취소", color = TeamFisColor.TextSecondary)
                }
            },
        ) {
            DatePicker(state = state)
        }
    }

    if (timePickerOpen) {
        val state = rememberTimePickerState(
            initialHour = at.hour,
            initialMinute = at.minute,
            is24Hour = false,
        )
        // 시각 고르개에는 `TimePickerDialog` 가 따로 없다 — 판을 직접 깐다
        BasicAlertDialog(onDismissRequest = { timePickerOpen = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
            ) {
                Column(Modifier.padding(TeamFisSpacing.xl)) {
                    TimePicker(state = state)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { timePickerOpen = false }) {
                            Text("취소", color = TeamFisColor.TextSecondary)
                        }
                        TextButton(onClick = {
                            at = at.withHour(state.hour).withMinute(state.minute)
                            timePickerOpen = false
                        }) { Text("고르기", color = TeamFisColor.Brand) }
                    }
                }
            }
        }
    }
}

/** 앞날·지난날 다 고를 수 있다. 기본값이지만 등록 화면의 `PastOnly` 와 짝을 맞춰 적어 둔다. */
@OptIn(ExperimentalMaterial3Api::class)
private object AnyDate : SelectableDates

private fun localDate(millis: Long): java.time.LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 일지를 주면 통째로 걷어낸다.
 * 끝난 수업에만 일지가 있다 — 노쇼는 한 게 없어서 쓸 것도 없다.
 */
private fun placeholderLog(item: ScheduleClass): ScheduleLog? =
    if (item.status != SessionStatus.Done) null
    else ScheduleLog(
        parts = listOf(BodyPart.Back, BodyPart.Arm, BodyPart.Cardio),
        note = "랫풀다운 자세가 많이 좋아졌어요. 다음 시간에는 중량을 올려 봐요.",
    )
