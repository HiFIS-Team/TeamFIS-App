package com.teamfis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.teamfis.app.ui.components.AddRowButton
import com.teamfis.app.ui.components.ClassTodo
import com.teamfis.app.ui.components.Member
import com.teamfis.app.ui.components.MemberDetail
import com.teamfis.app.ui.components.MemberGoalList
import com.teamfis.app.ui.components.MemberSectionHeader
import com.teamfis.app.ui.components.MemberSession
import com.teamfis.app.ui.components.ScheduleClass
import com.teamfis.app.ui.components.SessionCard
import com.teamfis.app.ui.components.SessionStatus
import com.teamfis.app.ui.components.Supplement
import com.teamfis.app.ui.components.SupplementEditDialog
import com.teamfis.app.ui.components.SupplementList
import com.teamfis.app.ui.components.SupplementPickerDialog
import com.teamfis.app.ui.components.placeholderMemberDetail
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import java.time.LocalDateTime

/**
 * 회원 일지 — 수업 탭의 일지 목록에서 회원 하나를 눌렀을 때.
 *
 * **HiFIS 회원 상세를 그대로 옮긴 화면이다** (2026-09-08 대표 지시) —
 * 운동을 하는 이유 → 회차 목록 + `회차 추가` → 영양제.
 *
 * **회원 상세와 일부러 갈라 두었다.** 거기는 누구인지·뭘 끊었는지 보는 자리고
 * 여기는 **적는 자리**다. 같은 회원을 두 화면이 그리지만 하는 일이 다르다.
 *
 * 회차는 **등록권을 안 가르고 한 줄로 세운다.** HiFIS 처럼 일지가 하나의 흐름이라
 * 여기서 등록권을 고르게 하면 지난 회차를 찾으러 위로 올라가야 한다.
 *
 * **잎 화면이다** — 셸의 `NavHost` 가 오른쪽에서 밀어 넣어 하단 탭 바까지 덮는다.
 */
@Composable
fun ClassMemberScreen(
    member: Member,
    onBack: () -> Unit,
    onLog: (ClassTodo) -> Unit = {},
) {
    val detail = remember(member) { placeholderMemberDetail(member) }

    // TODO(서버): 회원 상세 API 가 붙으면 받아 오고 고칠 때마다 보낸다
    val goals = remember(member) { mutableStateListOf("") }
    val supplements = remember(member) { mutableStateListOf<Supplement>() }
    var pickingSupplement by remember { mutableStateOf(false) }
    // 고치는 중인 영양제. 새로 담는 중이면 자리가 -1 이다
    var editingSupplement by remember { mutableStateOf<Pair<Int, Supplement>?>(null) }

    // 등록권을 가로질러 최근 회차부터 — 일지는 하나의 흐름이다
    val rounds = remember(detail) {
        detail.products
            .flatMap { product -> product.sessions.map { product to it } }
            .sortedByDescending { it.second.at }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        DetailHeader(title = "${detail.name} 회원님", onBack = onBack)

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = TeamFisSpacing.screenHorizontal),
        ) {
            MemberSectionHeader(
                "운동을 하는 이유",
                modifier = Modifier.padding(top = TeamFisSpacing.md),
            )
            MemberGoalList(
                goals = goals,
                onChange = { index, text -> goals[index] = text },
                onAdd = { goals.add("") },
                onRemove = {
                    // 마지막 한 줄은 비우기만 한다 — 판이 통째로 사라지면 다시
                    // 어디를 눌러야 할지 알 수 없다 (일지 표와 같은 규칙)
                    if (goals.size > 1) goals.removeAt(it) else goals[0] = ""
                },
            )

            MemberSectionHeader(
                "운동일지",
                modifier = Modifier.padding(top = TeamFisSpacing.xxxl),
            )
            Column(verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md)) {
                rounds.forEach { (product, session) ->
                    SessionCard(
                        session,
                        onClick = {
                            onLog(
                                ClassTodo(
                                    ScheduleClass(
                                        member = detail.name,
                                        at = session.at,
                                        minutes = ClassMinutes,
                                        product = product.name,
                                        progress = "${session.round}/${product.rounds}회차",
                                        status = session.status,
                                    ),
                                    parts = session.parts,
                                ),
                            )
                        },
                    )
                }

                // 아직 안 잡힌 수업을 뒤늦게 남길 때 — 마지막 다음 번호로 빈 일지를 연다.
                // 실제 번호는 서버가 매긴다 (두 대에서 동시에 눌러도 안 겹치게)
                AddRowButton("회차 추가", onClick = { onLog(newRound(detail)) })
            }

            MemberSectionHeader(
                "영양제",
                modifier = Modifier.padding(top = TeamFisSpacing.xxxl),
            )
            SupplementList(
                supplements = supplements,
                onEdit = { editingSupplement = it to supplements[it] },
                onAdd = { pickingSupplement = true },
                modifier = Modifier.padding(bottom = TeamFisSpacing.xxxl),
            )
        }
    }

    if (pickingSupplement) {
        SupplementPickerDialog(
            onPick = {
                supplements.add(it)
                pickingSupplement = false
            },
            onWriteMyself = {
                pickingSupplement = false
                editingSupplement = -1 to Supplement(name = "")
            },
            onDismiss = { pickingSupplement = false },
        )
    }

    editingSupplement?.let { (index, row) ->
        SupplementEditDialog(
            initial = row,
            editing = index >= 0,
            onSave = {
                if (index >= 0) supplements[index] = it else supplements.add(it)
                editingSupplement = null
            },
            onDelete = {
                if (index >= 0) supplements.removeAt(index)
                editingSupplement = null
            },
            onDismiss = { editingSupplement = null },
        )
    }
}

/** `회차 추가` — 가장 최근 등록권의 마지막 다음 번호로 빈 일지를 연다. */
private fun newRound(detail: MemberDetail): ClassTodo {
    val product = detail.products.first()
    val next = (product.sessions.maxOfOrNull(MemberSession::round) ?: 0) + 1
    return ClassTodo(
        ScheduleClass(
            member = detail.name,
            at = LocalDateTime.now(),
            minutes = ClassMinutes,
            product = product.name,
            progress = "$next/${product.rounds}회차",
            status = SessionStatus.Done,
        ),
    )
}

/** 수업 한 건의 길이. 일지는 시작 시각만 쓰지만 값이 있어야 넘길 수 있다. */
private const val ClassMinutes = 60
