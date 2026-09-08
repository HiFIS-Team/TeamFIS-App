package com.teamfis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
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
 * 일지 카드 — 운동 부위 칩 + 메모.
 *
 * **회원 상세의 회차 카드와 같은 판이다** (2026-09-08 대표 지시) — 같은 결의 내용이
 * 한 화면에서는 카드고 다른 화면에서는 맨바닥이면 안 읽힌다.
 *
 * 회차 카드가 부위만 보여 주는 것과 달리 **메모까지 편다.** 거기는 회차를 훑는
 * 목록이고 여기는 수업 하나만 있는 화면이라 접을 이유가 없다.
 *
 * **아직 안 쓴 수업에도 카드는 선다** (2026-09-08 대표 지시). 채워질 자리를 미리
 * 보여 주는 것이라 값만 비운다 — 상태마다 화면 생김새가 달라지면 안 된다.
 */
@Composable
fun ScheduleLogBlock(log: ScheduleLog?, modifier: Modifier = Modifier) {
    CardBlock("일지", modifier) {
        if (log == null || (log.parts.isEmpty() && log.note.isBlank())) {
            EmptyValue()
            return@CardBlock
        }

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

/**
 * 세션 사인 카드 — 회원이 회차를 확인하며 남긴 서명.
 *
 * 서명은 **회차를 깎았다는 증거**라 일지 바로 밑에 둔다. 일지가 무엇을 했는지라면
 * 사인은 그것을 회원이 확인했는지다.
 *
 * 그림은 **더 어두운 판 위에** 얹는다. 카드와 같은 색에 그리면 종이 위 잉크가 아니라
 * 그냥 떠 있는 선으로 보인다.
 */
@Composable
fun ScheduleSignBlock(signed: Boolean, modifier: Modifier = Modifier) {
    CardBlock("세션 사인", modifier) {
        if (!signed) {
            EmptyValue()
            return@CardBlock
        }

        Box(
            Modifier
                .padding(top = TeamFisSpacing.md)
                .fillMaxWidth()
                .height(SignHeight)
                .background(TeamFisColor.Surface2, TeamFisRadius.card),
        ) {
            Canvas(Modifier.fillMaxSize().padding(TeamFisSpacing.lg)) {
                drawPath(
                    path = signaturePath(size),
                    color = TeamFisColor.TextPrimary,
                    style = Stroke(
                        width = SignStrokeWidth.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
    }
}

/** 일지·사인이 같이 쓰는 판. 둘이 나란히 서므로 판이 어긋나면 바로 보인다. */
@Composable
private fun CardBlock(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .padding(TeamFisSpacing.lg),
    ) {
        Text(title, style = TeamFisType.titleSm, color = TeamFisColor.TextPrimary)
        content()
    }
}

/**
 * 값이 아직 없다는 표시.
 *
 * 회원 상세가 빈 칸에 쓰는 것과 같은 `—` 다. "아직 안 썼어요" 같은 문구를 새로 만들지
 * 않는다 — 앱에 이미 있는 말로 족하다.
 */
@Composable
private fun EmptyValue() {
    Text(
        "—",
        style = TeamFisType.bodySm,
        color = TeamFisColor.TextTertiary,
        modifier = Modifier.padding(top = TeamFisSpacing.md),
    )
}

/**
 * 자리 표시자 서명이다. 서버가 회원이 그린 그림을 주면 통째로 걷어낸다.
 *
 * 점은 0~1 로 적어 두고 칸 크기에 맞춰 늘린다 — 판 높이를 바꿔도 다시 안 그려도 된다.
 * 첫 점 하나에 이어 (제어점, 끝점) 짝이 붙는다.
 */
private fun signaturePath(size: Size): Path {
    val path = Path()
    val first = SignStroke.first()
    path.moveTo(first.first * size.width, first.second * size.height)

    var i = 1
    while (i + 1 < SignStroke.size) {
        val control = SignStroke[i]
        val end = SignStroke[i + 1]
        path.quadraticTo(
            control.first * size.width, control.second * size.height,
            end.first * size.width, end.second * size.height,
        )
        i += 2
    }
    return path
}

/**
 * 진폭도 간격도 **일부러 들쭉날쭉하다.** 고르게 두면 손글씨가 아니라 사인파로 보인다.
 * 마지막 한 획만 길게 빼 흘려 쓴 끝맺음을 흉내 낸다.
 */
private val SignStroke = listOf(
    0.05f to 0.55f,
    0.09f to 0.12f, 0.15f to 0.58f,
    0.19f to 0.90f, 0.24f to 0.30f,
    0.30f to 0.02f, 0.33f to 0.66f,
    0.36f to 0.95f, 0.44f to 0.48f,
    0.50f to 0.20f, 0.55f to 0.72f,
    0.60f to 0.98f, 0.67f to 0.35f,
    0.73f to 0.08f, 0.78f to 0.60f,
    0.84f to 0.92f, 0.97f to 0.22f,
)

private val SignHeight = 96.dp
private val SignStrokeWidth = 2.5.dp

/** `3월 21일 (토)` — 수업 상세의 머리에 선다. */
fun dayTitle(date: LocalDate): String =
    "${date.monthValue}월 ${date.dayOfMonth}일 (${date.dayOfWeek.koLabel})"

/** `2026. 3. 21. (토)` — 회원 상세의 날짜 표기에 요일만 더했다. 무슨 요일 수업인지가 중요해서다. */
fun dayLabel(date: LocalDate): String =
    "${date.year}. ${date.monthValue}. ${date.dayOfMonth}. (${date.dayOfWeek.koLabel})"

/** 날짜만 갈아 끼운다 — 시각은 그대로 둔다. */
fun LocalDateTime.onDate(date: LocalDate): LocalDateTime =
    LocalDateTime.of(date, toLocalTime())
