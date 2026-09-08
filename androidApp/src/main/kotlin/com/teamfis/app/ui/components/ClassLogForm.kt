package com.teamfis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSize
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 웨이트 한 줄 — 부위 · 운동명 · 무게 · 횟수 · 세트.
 *
 * **숫자와 단위를 갈라 둔다** (HiFIS 에서 가져온 규칙). `60kg 12회` 를 통째로 치면
 * 한글↔숫자 자판을 오가야 한다. 단위가 칸에 붙박이면 자판이 숫자판으로 고정된다.
 */
data class WeightEntry(
    val part: BodyPart? = null,
    val name: String = "",
    val weight: String = "",
    val reps: String = "",
    val sets: String = "",
)

/** 유산소 한 줄 — 운동명 · 시간(분). */
data class CardioEntry(
    val name: String = "",
    val minutes: String = "",
)

/**
 * 웨이트 줄 하나.
 *
 * **두 줄로 눕힌다.** 부위·운동명·무게·횟수·세트를 한 줄에 넣으면 폰에서 칸이
 * 손톱만 해진다. 위는 무슨 운동인지, 아래는 얼마나 했는지다.
 *
 * 왼쪽 번호는 **줄이 늘어졌을 때 어디까지 적었는지** 놓치지 않게 하는 것이다.
 */
@Composable
fun WeightRowFields(
    entry: WeightEntry,
    number: Int,
    onChange: (WeightEntry) -> Unit,
    onPickPart: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        IndexBadge(number)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = TeamFisSpacing.sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PartCell(entry.part, onPickPart)
                Spacer(Modifier.width(TeamFisSpacing.sm))
                CellField(
                    value = entry.name,
                    hint = "운동명",
                    onValueChange = { onChange(entry.copy(name = it)) },
                    modifier = Modifier.weight(1f),
                )
                RemoveButton(onRemove)
            }

            Row(
                modifier = Modifier.padding(top = TeamFisSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UnitField(
                    value = entry.weight,
                    unit = "kg",
                    decimal = true,
                    onValueChange = { onChange(entry.copy(weight = it)) },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(TeamFisSpacing.sm))
                UnitField(
                    value = entry.reps,
                    unit = "회",
                    onValueChange = { onChange(entry.copy(reps = it)) },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(TeamFisSpacing.sm))
                SetsStepper(
                    value = entry.sets,
                    onValueChange = { onChange(entry.copy(sets = it)) },
                )
                // 윗줄 지우기 버튼만큼 비운다 — 칸 끝이 어긋나면 표로 안 보인다
                Spacer(Modifier.width(RemoveSize))
            }
        }
    }
}

/** 유산소 줄 하나 — 웨이트와 달리 한 줄에 다 들어간다. */
@Composable
fun CardioRowFields(
    entry: CardioEntry,
    onChange: (CardioEntry) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        CellField(
            value = entry.name,
            hint = "운동명",
            onValueChange = { onChange(entry.copy(name = it)) },
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(TeamFisSpacing.sm))
        UnitField(
            value = entry.minutes,
            unit = "분",
            onValueChange = { onChange(entry.copy(minutes = it)) },
            modifier = Modifier.width(TimeWidth),
        )
        RemoveButton(onRemove)
    }
}

/** 표를 감싸는 판 — 줄이 여럿일 때 한 덩어리로 보여야 한다. */
@Composable
fun TableBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .padding(TeamFisSpacing.sm),
    ) {
        content()
    }
}

/** `+ 운동 추가` — 표 밑에 붙는 줄. */
@Composable
fun AddRowButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(TeamFisSize.minTouchTarget)
            .clip(TeamFisRadius.card)
            .border(1.dp, TeamFisColor.Divider, TeamFisRadius.card)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = null, // 옆 글자가 이름 역할을 한다
            tint = TeamFisColor.TextSecondary,
            modifier = Modifier.size(16.dp),
        )
        Text(
            label,
            style = TeamFisType.bodySm,
            color = TeamFisColor.TextSecondary,
            modifier = Modifier.padding(start = TeamFisSpacing.xs),
        )
    }
}

/**
 * 운동 부위 고르개.
 *
 * **직접 입력을 안 받는다** — HiFIS 는 자유 글자라 아무 말이나 들어갔는데,
 * 우리는 [BodyPart] 여섯 갈래로 굳혀 두었고 조회 화면이 그 아이콘으로 그린다.
 * 없는 부위를 손으로 적게 두면 그 줄만 아이콘이 없다.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BodyPartDialog(selected: BodyPart?, onPick: (BodyPart?) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(TeamFisColor.Surface1, TeamFisRadius.card)
                .padding(TeamFisSpacing.xl),
        ) {
            Text("운동 부위", style = TeamFisType.titleSm, color = TeamFisColor.TextPrimary)

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = TeamFisSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
            ) {
                // 유산소는 아래 제 표가 따로 있다 — 여기 두면 어디에 적을지 갈린다
                BodyPart.entries.filter { it != BodyPart.Cardio }.forEach { part ->
                    val isSelected = part == selected
                    val interaction = remember(part) { MutableInteractionSource() }
                    Row(
                        modifier = Modifier
                            .height(TeamFisSize.chip)
                            .clip(TeamFisRadius.card)
                            .background(
                                if (isSelected) TeamFisColor.Brand else TeamFisColor.Surface2,
                            )
                            .clickable(
                                interactionSource = interaction,
                                indication = null,
                                onClick = { onPick(part) },
                            )
                            .padding(horizontal = TeamFisSpacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.xs),
                    ) {
                        Icon(
                            painter = painterResource(part.icon),
                            contentDescription = null, // 옆 글자가 이름 역할을 한다
                            tint = if (isSelected) {
                                TeamFisColor.TextPrimary
                            } else {
                                TeamFisColor.TextSecondary
                            },
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            part.label,
                            style = TeamFisType.bodySm,
                            color = if (isSelected) {
                                TeamFisColor.TextPrimary
                            } else {
                                TeamFisColor.TextSecondary
                            },
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = TeamFisSpacing.xl),
                horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                SessionButton("비우기", TeamFisColor.Surface2, TeamFisColor.TextSecondary) {
                    onPick(null)
                }
                SessionButton("닫기", TeamFisColor.Surface2, TeamFisColor.TextSecondary, onDismiss)
            }
        }
    }
}

// MARK: - 칸 조각들

/** 표 안의 글자 칸 — 화면 폼의 [FormField] 보다 낮고 좁다. */
@Composable
private fun CellField(
    value: String,
    hint: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TeamFisType.bodySm.copy(color = TeamFisColor.TextPrimary),
        cursorBrush = SolidColor(TeamFisColor.Brand),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.height(CellHeight),
        decorationBox = { inner ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(CellHeight)
                    .clip(TeamFisRadius.card)
                    .background(TeamFisColor.Surface2)
                    .padding(horizontal = TeamFisSpacing.md),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(hint, style = TeamFisType.bodySm, color = TeamFisColor.TextMuted)
                }
                inner()
            }
        },
    )
}

/**
 * 숫자만 치는 칸 — **단위가 칸에 붙박이로 적혀 있다.**
 *
 * 값이 비어 있어도 단위는 옅게 남는다. 여기가 무슨 자리인지가 그것으로 드러난다.
 */
@Composable
private fun UnitField(
    value: String,
    unit: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    decimal: Boolean = false,
) {
    BasicTextField(
        value = value,
        onValueChange = { text ->
            val allowed = text.filter { it.isDigit() || (decimal && it == '.') }
            onValueChange(allowed.take(6))
        },
        textStyle = TeamFisType.bodySm.copy(color = TeamFisColor.TextPrimary),
        cursorBrush = SolidColor(TeamFisColor.Brand),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number,
        ),
        modifier = modifier.height(CellHeight),
        decorationBox = { inner ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(CellHeight)
                    .clip(TeamFisRadius.card)
                    .background(TeamFisColor.Surface2)
                    .padding(horizontal = TeamFisSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) { inner() }
                Text(
                    unit,
                    style = TeamFisType.caption,
                    color = if (value.isEmpty()) TeamFisColor.TextMuted else TeamFisColor.TextTertiary,
                )
            }
        },
    )
}

/**
 * 세트 — **눌러서 올리고 내린다.**
 *
 * 세트는 3·4·5 근처에서 맴돌아 자판을 띄울 값이 아니다. 다만 20세트 같은 것도
 * 있으니 가운데는 그대로 칠 수 있게 둔다.
 */
@Composable
private fun SetsStepper(value: String, onValueChange: (String) -> Unit) {
    fun step(delta: Int) {
        val next = ((value.toIntOrNull() ?: 0) + delta).coerceIn(0, 99)
        onValueChange(if (next == 0) "" else "$next")
    }

    Row(
        modifier = Modifier
            .width(SetsWidth)
            .height(CellHeight)
            .clip(TeamFisRadius.card)
            .background(TeamFisColor.Surface2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepButton(R.drawable.ic_minus, "세트 줄이기") { step(-1) }

        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it.filter(Char::isDigit).take(2)) },
            textStyle = TeamFisType.bodySm.copy(
                color = TeamFisColor.TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            ),
            cursorBrush = SolidColor(TeamFisColor.Brand),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.Center) {
                    if (value.isEmpty()) {
                        Text("세트", style = TeamFisType.caption, color = TeamFisColor.TextMuted)
                    }
                    inner()
                }
            },
        )

        StepButton(R.drawable.ic_plus, "세트 늘리기") { step(1) }
    }
}

@Composable
private fun StepButton(icon: Int, description: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .width(StepWidth)
            .height(CellHeight)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = TeamFisColor.TextSecondary,
            modifier = Modifier.size(14.dp),
        )
    }
}

/** 부위 칸 — 누르면 고르개가 뜬다. 고르기 전에는 `부위` 라고만 적혀 있다. */
@Composable
private fun PartCell(part: BodyPart?, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .width(PartWidth)
            .height(CellHeight)
            .clip(TeamFisRadius.card)
            .background(TeamFisColor.Surface2)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (part == null) {
            Text("부위", style = TeamFisType.caption, color = TeamFisColor.TextMuted)
        } else {
            Icon(
                painter = painterResource(part.icon),
                contentDescription = null, // 옆 글자가 이름 역할을 한다
                tint = TeamFisColor.TextSecondary,
                modifier = Modifier.size(12.dp),
            )
            Text(
                part.label,
                style = TeamFisType.caption,
                color = TeamFisColor.TextSecondary,
                modifier = Modifier.padding(start = 2.dp),
            )
        }
    }
}

/** 몇 번째 운동인지 — 줄이 늘어지면 어디까지 적었는지 놓친다. */
@Composable
private fun IndexBadge(number: Int) {
    Box(
        Modifier
            .padding(top = (CellHeight - IndexSize) / 2)
            .size(IndexSize)
            .clip(TeamFisRadius.card)
            .background(TeamFisColor.Surface2),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "$number",
            style = TeamFisType.caption.copy(fontFeatureSettings = "tnum"),
            color = TeamFisColor.TextTertiary,
        )
    }
}

@Composable
private fun RemoveButton(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(RemoveSize)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "이 줄 지우기",
            tint = TeamFisColor.TextTertiary,
            modifier = Modifier.size(14.dp),
        )
    }
}

/** 표 칸 높이 — 화면 폼(52)보다 낮다. 한 줄에 여럿이 서서 그만큼 빽빽하다. */
private val CellHeight = 40.dp
private val PartWidth = 66.dp
private val SetsWidth = 92.dp
private val StepWidth = 26.dp
private val TimeWidth = 84.dp
private val RemoveSize = 28.dp
private val IndexSize = 20.dp
