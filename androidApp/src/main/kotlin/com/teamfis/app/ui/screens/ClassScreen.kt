package com.teamfis.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teamfis.app.ui.components.BodyPart
import com.teamfis.app.ui.components.ClassCard
import com.teamfis.app.ui.components.ClassFilter
import com.teamfis.app.ui.components.ClassFilterBar
import com.teamfis.app.ui.components.ClassTodo
import com.teamfis.app.ui.components.Member
import com.teamfis.app.ui.components.MemberDivider
import com.teamfis.app.ui.components.MemberRow
import com.teamfis.app.ui.components.ScheduleClass
import com.teamfis.app.ui.components.SessionStatus
import com.teamfis.app.ui.components.placeholderMembers
import com.teamfis.app.ui.shell.AppHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.LocalDate

/**
 * 수업.
 *
 * 트레이너가 수업 뒤에 할 일은 **일지를 쓰는 것**과 **회원에게 사인을 받는 것**
 * 둘뿐이라 필터도 둘이다. 다만 **두 갈래가 서로 다른 것을 나열한다** —
 *
 * - **일지** — 보유 회원 전체. 회원을 고르고 그 안에서 회차를 편다
 *   (HiFIS 와 같은 길, 2026-09-08 대표 지시). 지난 회차를 다시 펴야 하는 일이라
 *   밀린 것만 모아 두면 이미 쓴 일지를 찾을 수 없다
 * - **세션 사인** — 밀린 것만. 받아야 할 것이 남았는지가 전부라 회원을 거칠 이유가 없다
 *
 * 목록이 길어지므로 필터 줄은 **위에 고정**한다 (회원 목록과 같은 이유).
 */
@Composable
fun ClassScreen(
    onMember: (Member) -> Unit = {},
    onSign: (ClassTodo) -> Unit = {},
    onNotification: () -> Unit = {},
    onMy: () -> Unit = {},
) {
    var filter by rememberSaveable { mutableStateOf(ClassFilter.Log) }

    val pending = remember { pendingSigns() }
    val counts = mapOf(
        ClassFilter.Log to placeholderMembers.size,
        ClassFilter.Sign to pending.size,
    )

    Column(Modifier.fillMaxSize()) {
        AppHeader(onNotification = onNotification, onMy = onMy)

        ClassFilterBar(
            selected = filter,
            counts = counts,
            onSelect = { filter = it },
            modifier = Modifier.padding(
                top = TeamFisSpacing.sm,
                bottom = TeamFisSpacing.md,
            ),
        )

        when (filter) {
            // 회원 줄은 회원 탭 것을 그대로 쓴다 — 같은 사람이 화면마다 다르게
            // 생기면 안 된다. 여기서는 누르면 상세가 아니라 그 회원의 일지로 간다
            ClassFilter.Log -> LazyColumn(
                contentPadding = PaddingValues(bottom = TeamFisSpacing.xxxl),
            ) {
                itemsIndexed(placeholderMembers) { index, member ->
                    if (index > 0) MemberDivider()
                    MemberRow(member, onClick = { onMember(member) })
                }
            }

            ClassFilter.Sign -> if (pending.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "받을 사인이 없어요",
                        style = TeamFisType.bodySm,
                        color = TeamFisColor.TextMuted,
                    )
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
                    items(pending) { todo -> ClassCard(todo, onClick = { onSign(todo) }) }
                }
            }
        }
    }
}

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 밀린 사인을 주면 통째로 걷어낸다.
 *
 * **끝난 수업에 일지까지 있는 것만** 온다 — 무엇을 했는지 적어야 회원이 그것을
 * 확인해 줄 수 있어서다 (2026-09-08 대표 지시).
 */
private fun pendingSigns(): List<ClassTodo> {
    val today = LocalDate.now()
    return listOf(
        ClassTodo(
            ScheduleClass(
                "000", today.minusDays(1).atTime(11, 0), 60,
                "얼리버드 10회", "8/10회차", SessionStatus.Done,
            ),
            parts = listOf(BodyPart.Back, BodyPart.Arm, BodyPart.Cardio),
        ),
        ClassTodo(
            ScheduleClass(
                "000", today.minusDays(3).atTime(20, 0), 60,
                "PT 30회", "24/30회차", SessionStatus.Done,
            ),
            parts = listOf(BodyPart.Chest, BodyPart.Shoulder),
        ),
    )
}
