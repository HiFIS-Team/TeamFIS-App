package com.teamfis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 손으로 긋는 서명 칸.
 *
 * 획을 **화면이 들고 있고** 칸은 받아 그리기만 한다. 지우기·저장이 밖에 있어서다 —
 * 칸이 제 안에 숨겨 두면 밖에서 비울 수도 보낼 수도 없다.
 *
 * 한 획은 점의 나열이고, 획이 끝날 때마다 [strokes] 에 쌓인다. 그리는 중인 획만
 * [current] 로 따로 온다 — 매번 전체를 다시 만들지 않으려는 것이다.
 */
@Composable
fun SignaturePad(
    strokes: List<List<Offset>>,
    current: List<Offset>,
    onStrokeStart: (Offset) -> Unit,
    onStrokeMove: (Offset) -> Unit,
    onStrokeEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(TeamFisRadius.card)
            .background(TeamFisColor.Surface1)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = onStrokeStart,
                    onDrag = { change, _ ->
                        onStrokeMove(change.position)
                        // 안 먹으면 뒤의 스크롤이 같이 움직인다
                        change.consume()
                    },
                    onDragEnd = onStrokeEnd,
                    onDragCancel = onStrokeEnd,
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        // 빈 판은 그릴 수 있는 자리로 안 보인다. 한 줄만 두고 첫 획에 사라진다
        if (strokes.isEmpty() && current.isEmpty()) {
            Text(
                "손가락으로 서명해 주세요",
                style = TeamFisType.bodySm,
                color = TeamFisColor.TextMuted,
            )
        }

        Canvas(Modifier.fillMaxSize()) {
            strokes.forEach { drawStroke(it) }
            drawStroke(current)
        }
    }
}

/**
 * 점을 이어 한 획을 긋는다.
 *
 * 점끼리 직선으로 이으면 손이 꺾인 자리마다 각이 진다. **점을 제어점으로 삼고
 * 이웃한 두 점의 가운데를 지나게** 해서 곡선으로 편다.
 */
private fun DrawScope.drawStroke(points: List<Offset>) {
    if (points.size < 2) return

    val path = Path()
    path.moveTo(points[0].x, points[0].y)
    for (i in 1 until points.size) {
        val previous = points[i - 1]
        val point = points[i]
        path.quadraticTo(
            previous.x, previous.y,
            (previous.x + point.x) / 2, (previous.y + point.y) / 2,
        )
    }
    path.lineTo(points.last().x, points.last().y)

    drawPath(
        path = path,
        color = TeamFisColor.TextPrimary,
        style = Stroke(
            width = StrokeWidth.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}

private val StrokeWidth = 3.dp
