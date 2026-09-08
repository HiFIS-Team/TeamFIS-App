package com.teamfis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teamfis.app.ui.components.AddRowButton
import com.teamfis.app.ui.components.BodyPartDialog
import com.teamfis.app.ui.components.BottomActionButton
import com.teamfis.app.ui.components.CardioEntry
import com.teamfis.app.ui.components.CardioRowFields
import com.teamfis.app.ui.components.ClassTodo
import com.teamfis.app.ui.components.FieldLabel
import com.teamfis.app.ui.components.FormField
import com.teamfis.app.ui.components.PickerField
import com.teamfis.app.ui.components.TableBox
import com.teamfis.app.ui.components.WeightEntry
import com.teamfis.app.ui.components.WeightRowFields
import com.teamfis.app.ui.components.ampmTime
import com.teamfis.app.ui.components.dayLabel
import com.teamfis.app.ui.components.dayTitle
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.Instant
import java.time.ZoneId

/**
 * 일지 작성 — 수업 탭의 일지 목록에서 한 건을 눌렀을 때.
 *
 * **HiFIS 의 운동 일지 서식을 가져왔다** (2026-09-08 대표 지시) — 수업 내용 ·
 * 웨이트 표 · 유산소 표 · 피드백. 생김새만 TeamFIS 것으로 갈았다.
 *
 * `수업 날짜` 는 **수업에서 이미 정해져 온다.** 그래도 칸을 두는 것은 잘못 잡힌 날에
 * 수업한 것을 여기서 바로잡을 수 있어야 해서다 (2026-09-08 대표 지시로 되살렸다).
 *
 * **아직 못 가져온 것은 `사진 · 영상` 하나다** — 올릴 곳이 없다.
 *
 * 머리 모양은 세션 사인 화면과 같다 — 형제 화면이라 나란해야 한다.
 *
 * **잎 화면이다** — 셸의 `NavHost` 가 오른쪽에서 밀어 넣어 하단 탭 바까지 덮는다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassLogScreen(todo: ClassTodo, onBack: () -> Unit) {
    var title by remember(todo) { mutableStateOf("") }
    var feedback by remember(todo) { mutableStateOf("") }
    var at by remember(todo) { mutableStateOf(todo.item.at.toLocalDate()) }
    var datePickerOpen by remember { mutableStateOf(false) }

    // 처음부터 빈 줄 하나씩 둔다 — 누르지 않아도 바로 적는다 (HiFIS 와 같은 규칙)
    val weights = remember(todo) { mutableStateListOf(WeightEntry()) }
    val cardio = remember(todo) { mutableStateListOf(CardioEntry()) }

    // 부위 고르개를 띄운 줄. 없으면 안 뜬다
    var partRow by remember { mutableIntStateOf(-1) }

    Column(
        Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            // 자판이 올라와도 적고 있는 칸이 가리지 않아야 한다
            .imePadding(),
    ) {
        DetailHeader(title = "일지 작성", onBack = onBack)

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = TeamFisSpacing.screenHorizontal),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${todo.item.member} 회원님",
                    style = TeamFisType.titleLg,
                    color = TeamFisColor.TextPrimary,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    todo.item.progress,
                    style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
                    color = TeamFisColor.TextSecondary,
                )
            }

            Text(
                "${dayTitle(todo.item.at.toLocalDate())} ${ampmTime(todo.item.at)}",
                style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
                color = TeamFisColor.TextSecondary,
                modifier = Modifier.padding(top = TeamFisSpacing.sm),
            )

            Spacer(Modifier.height(TeamFisSpacing.xl))

            FieldLabel("수업 내용")
            FormField(
                value = title,
                onValueChange = { title = it },
                hint = "예) 가슴, 삼두",
            )

            Spacer(Modifier.height(TeamFisSpacing.xl))

            FieldLabel("수업 날짜")
            PickerField(
                label = "수업 날짜",
                value = dayLabel(at),
                onTap = { datePickerOpen = true },
            )

            Spacer(Modifier.height(TeamFisSpacing.xl))

            FieldLabel("웨이트 운동")
            TableBox {
                weights.forEachIndexed { index, entry ->
                    if (index > 0) Spacer(Modifier.height(TeamFisSpacing.md))
                    WeightRowFields(
                        entry = entry,
                        number = index + 1,
                        onChange = { weights[index] = it },
                        onPickPart = { partRow = index },
                        onRemove = {
                            // 마지막 한 줄은 비우기만 한다 — 표가 통째로 사라지면
                            // 다시 어디를 눌러야 할지 알 수 없다
                            if (weights.size > 1) weights.removeAt(index)
                            else weights[0] = WeightEntry()
                        },
                    )
                }
            }
            AddRowButton(
                "운동 추가",
                onClick = { weights.add(WeightEntry()) },
                modifier = Modifier.padding(top = TeamFisSpacing.sm),
            )

            Spacer(Modifier.height(TeamFisSpacing.xl))

            FieldLabel("유산소 운동")
            TableBox {
                cardio.forEachIndexed { index, entry ->
                    if (index > 0) Spacer(Modifier.height(TeamFisSpacing.sm))
                    CardioRowFields(
                        entry = entry,
                        onChange = { cardio[index] = it },
                        onRemove = {
                            if (cardio.size > 1) cardio.removeAt(index)
                            else cardio[0] = CardioEntry()
                        },
                    )
                }
            }
            AddRowButton(
                "유산소 추가",
                onClick = { cardio.add(CardioEntry()) },
                modifier = Modifier.padding(top = TeamFisSpacing.sm),
            )

            Spacer(Modifier.height(TeamFisSpacing.xl))

            FieldLabel("피드백")
            FormField(
                value = feedback,
                onValueChange = { feedback = it },
                hint = "오늘 수업에서 느낀 점 · 다음에 볼 것",
                lines = 4,
            )

            Spacer(Modifier.height(TeamFisSpacing.xxxl))
        }

        Column(
            Modifier.padding(
                horizontal = TeamFisSpacing.screenHorizontal,
                vertical = TeamFisSpacing.md,
            ),
        ) {
            // 수업 내용만 있으면 보낼 수 있다 — 표는 비워 두고 글로만 적는 날도 있다
            BottomActionButton(
                label = "저장",
                filled = title.isNotBlank(),
                // TODO(서버): 일지 저장 API 가 붙으면 실제로 보낸다
                onClick = {},
            )
        }
    }

    if (datePickerOpen) {
        // 앞날은 못 고른다 — 이미 한 수업을 적는 자리다
        val state = rememberDatePickerState(
            initialSelectedDateMillis = at
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            selectableDates = LogPastOnly,
        )
        DatePickerDialog(
            onDismissRequest = { datePickerOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        at = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                    }
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

    if (partRow >= 0) {
        val row = partRow
        BodyPartDialog(
            selected = weights.getOrNull(row)?.part,
            onPick = { part ->
                weights.getOrNull(row)?.let { weights[row] = it.copy(part = part) }
                partRow = -1
            },
            onDismiss = { partRow = -1 },
        )
    }
}

/** 앞날은 못 고른다 — 이미 한 수업을 적는 자리다 (등록 화면의 `PastOnly` 와 같은 뜻). */
@OptIn(ExperimentalMaterial3Api::class)
private object LogPastOnly : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long) =
        utcTimeMillis <= System.currentTimeMillis()
}
