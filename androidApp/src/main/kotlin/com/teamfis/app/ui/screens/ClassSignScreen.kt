package com.teamfis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.teamfis.app.ui.components.BodyPartChips
import com.teamfis.app.ui.components.ClassTodo
import com.teamfis.app.ui.components.SessionButton
import com.teamfis.app.ui.components.SignaturePad
import com.teamfis.app.ui.components.ampmTime
import com.teamfis.app.ui.components.dayTitle
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 세션 사인 — 수업 탭의 사인 목록에서 한 건을 눌렀을 때.
 *
 * **회원이 직접 긋는 화면이다.** 트레이너가 폰을 넘겨 주는 자리라, 위에는 무엇을
 * 확인하는 건지(누구·언제·무슨 운동)만 짧게 서고 **나머지 높이는 전부 서명 칸**이다.
 * 칸이 좁으면 이름이 안 써진다.
 *
 * 지우기·저장은 회차 처리 버튼과 같은 모양이다 — 화면 아래에 나란히 선다.
 *
 * **잎 화면이다** — 셸의 `NavHost` 가 오른쪽에서 밀어 넣어 하단 탭 바까지 덮는다.
 */
@Composable
fun ClassSignScreen(todo: ClassTodo, onBack: () -> Unit) {
    // 획은 여기서 든다 — 지우기·저장이 칸 밖에 있어서다
    var strokes by remember(todo) { mutableStateOf<List<List<Offset>>>(emptyList()) }
    var current by remember(todo) { mutableStateOf<List<Offset>>(emptyList()) }
    val signed = strokes.isNotEmpty()

    Column(
        Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        DetailHeader(title = "세션 사인", onBack = onBack)

        Column(
            Modifier
                .fillMaxSize()
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

            // 오늘 무엇을 했는지 — 회원이 그걸 보고 확인하는 것이라 사인 칸 위에 있어야 한다
            if (todo.parts.isNotEmpty()) {
                BodyPartChips(todo.parts, modifier = Modifier.padding(top = TeamFisSpacing.md))
            }

            SignaturePad(
                strokes = strokes,
                current = current,
                onStrokeStart = { current = listOf(it) },
                onStrokeMove = { current = current + it },
                onStrokeEnd = {
                    if (current.size > 1) strokes = strokes + listOf(current)
                    current = emptyList()
                },
                modifier = Modifier
                    .padding(top = TeamFisSpacing.lg)
                    .fillMaxWidth()
                    // 남는 높이를 전부 준다 — 이름을 쓸 만큼은 돼야 한다
                    .weight(1f),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = TeamFisSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                SessionButton("지우기", TeamFisColor.Surface2, TeamFisColor.TextSecondary) {
                    strokes = emptyList()
                    current = emptyList()
                }
                // 안 그었으면 보낼 것이 없다 — 눌리게 두되 빈 채로 둔다
                SessionButton(
                    label = "저장",
                    background = if (signed) TeamFisColor.Brand else TeamFisColor.Surface2,
                    contentColor = if (signed) TeamFisColor.TextPrimary else TeamFisColor.TextTertiary,
                    // TODO(서버): 사인 저장 API 가 붙으면 그림을 보낸다
                    onClick = {},
                )
            }

            Spacer(Modifier.height(TeamFisSpacing.xs))
        }
    }
}
