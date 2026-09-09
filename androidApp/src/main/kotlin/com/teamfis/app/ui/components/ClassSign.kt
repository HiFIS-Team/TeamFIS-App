package com.teamfis.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisMotion
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 서명 앞에 받는 동의 줄.
 *
 * **서명은 개인정보다.** 회원 폰이 아니라 트레이너 폰에 남기므로 더더욱 받아 두고
 * 시작해야 한다 (2026-09-09 대표 지시).
 *
 * 회원이 읽고 누르는 줄이라 **말이 회원 것**이다 — `동의합니다`.
 */
@Composable
fun ConsentRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val box by animateColorAsState(
        targetValue = if (checked) TeamFisColor.Brand else TeamFisColor.Surface2,
        animationSpec = TeamFisMotion.fast(),
        label = "consentBox",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(TeamFisRadius.card)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = { onCheckedChange(!checked) },
            )
            .padding(vertical = TeamFisSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(BoxSize)
                .clip(TeamFisRadius.card)
                .background(box),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null, // 옆 글자가 이름 역할을 한다
                    tint = TeamFisColor.TextPrimary,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        Text(
            "오늘 수업을 받았음을 확인하고, 서명 수집에 동의합니다",
            style = TeamFisType.bodySm,
            color = TeamFisColor.TextSecondary,
            modifier = Modifier.padding(start = TeamFisSpacing.md),
        )
    }
}

/**
 * 손으로 긋는 서명 칸.
 *
 * 획을 **화면이 들고 있고** 칸은 받아 그리기만 한다. 지우기·저장이 밖에 있어서다 —
 * 칸이 제 안에 숨겨 두면 밖에서 비울 수도 보낼 수도 없다.
 *
 * 한 획은 점의 나열이고, 획이 끝날 때마다 [strokes] 에 쌓인다. 그리는 중인 획만
 * [current] 로 따로 온다 — 매번 전체를 다시 만들지 않으려는 것이다.
 *
 * **동의 전에는 안 열린다** ([enabled], 2026-09-09 대표 지시). 손이 안 먹고 판이
 * 가라앉는다 — 잠긴 것이 보여야 위의 동의 줄을 찾는다.
 */
@Composable
fun SignaturePad(
    strokes: List<List<Offset>>,
    current: List<Offset>,
    onStrokeStart: (Offset) -> Unit,
    onStrokeMove: (Offset) -> Unit,
    onStrokeEnd: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier
            .clip(TeamFisRadius.card)
            // ⚠️ `Modifier.alpha` 를 쓰면 안 된다. 그건 그래픽 레이어를 세우는데,
            // 그 앞에 선 `background` 와 엉켜 **켰을 때 판이 되레 사라졌다**
            // (2026-09-09 대표 확인). 색 자체의 투명도로 낮춘다
            .background(
                if (enabled) TeamFisColor.Surface1
                else TeamFisColor.Surface1.copy(alpha = LockedAlpha),
            )
            // 손이 안 먹는 것도 `enabled` 를 열쇠로 다시 잡는다 — 모디파이어 사슬이
            // 상태마다 달라지면 위와 같은 일이 또 난다
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
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
        // 빈 판은 그릴 수 있는 자리로 안 보인다. 한 줄만 두고 첫 획에 사라진다.
        // **잠겼으면 무엇을 해야 하는지로 바뀐다** (2026-09-09 대표 지시) —
        // 어두운 판만 있으면 왜 안 그려지는지 알 수 없다.
        // 판은 가라앉아도 이 줄은 안 흐리게 둔다. 읽으라고 둔 글자다
        if (strokes.isEmpty() && current.isEmpty()) {
            Text(
                if (enabled) "손가락으로 서명해 주세요" else "위 동의란에 체크해 주세요",
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

/** 잠긴 판의 흐림 — 있는 것은 보이되 만질 것이 아니라는 만큼 */
private const val LockedAlpha = 0.4f

private val BoxSize = 22.dp
