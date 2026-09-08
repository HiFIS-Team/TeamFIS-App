package com.teamfis.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisMotion
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSize
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/** 칸 이름 — 입력칸 바로 위에 붙는다. */
@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = TeamFisType.bodySm,
        color = TeamFisColor.TextSecondary,
        modifier = modifier.padding(start = TeamFisSpacing.xs, bottom = TeamFisSpacing.sm),
    )
}

/**
 * 글자 입력칸.
 *
 * Material 기본 `TextField` 를 안 쓴다 — 밑줄·라벨 띄우기·자체 여백이 딸려 와서
 * 우리 각진 판 모양과 안 맞는다.
 */
@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    lines: Int = 1,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TeamFisType.body.copy(color = TeamFisColor.TextPrimary),
        cursorBrush = SolidColor(TeamFisColor.Brand),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = lines == 1,
        minLines = lines,
        maxLines = lines,
        modifier = modifier
            .fillMaxWidth()
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .padding(horizontal = TeamFisSpacing.lg, vertical = FieldVertical),
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    Text(hint, style = TeamFisType.body, color = TeamFisColor.TextTertiary)
                }
                inner()
            }
        },
    )
}

/**
 * 눌러서 고르는 칸 — 날짜·회원처럼 **손으로 못 적는 값**.
 *
 * 비었으면 칸 이름이 흐리게 서 있고, 고르면 그 자리에 값이 들어온다.
 * 값이 있으면 오른쪽이 `×` 로 바뀌어 비울 수 있다.
 */
@Composable
fun PickerField(
    label: String,
    value: String?,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(TeamFisRadius.card)
            .background(TeamFisColor.Surface1)
            .clickable(interactionSource = interaction, indication = null, onClick = onTap)
            .padding(start = TeamFisSpacing.lg, end = TeamFisSpacing.sm)
            .height(FieldHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            value ?: label,
            style = TeamFisType.body,
            color = if (value == null) TeamFisColor.TextTertiary else TeamFisColor.TextPrimary,
        )

        Spacer(Modifier.weight(1f))

        if (value != null && onClear != null) {
            val clearInteraction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(TeamFisSize.minTouchTarget)
                    .clickable(
                        interactionSource = clearInteraction,
                        indication = null,
                        onClick = onClear,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "비우기",
                    tint = TeamFisColor.TextTertiary,
                    modifier = Modifier.size(18.dp),
                )
            }
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null, // 왼쪽 글자가 이름 역할을 한다
                tint = TeamFisColor.TextTertiary,
                modifier = Modifier
                    .padding(end = TeamFisSpacing.sm)
                    .size(18.dp),
            )
        }
    }
}

/**
 * 모드 고르개 — 트랙 위에서 **알약 하나가 미끄러진다.**
 *
 * 칸마다 따로 켜고 끄면 옮기는 동안 둘 다 켜져 보이거나 툭 튄다.
 *
 * 브랜드 레드를 안 쓴다. 필터 칩은 **골라도 되고 안 골라도 되는** 것이라 선택이
 * 튀어야 하지만, 여기는 늘 둘 중 하나가 켜져 있다 — 자리만 알려 주면 된다.
 * 빨강은 아래 등록 버튼이 가져간다.
 */
@Composable
fun SegmentedTabs(
    labels: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(TeamFisSize.segment)
            .clip(TeamFisRadius.card)
            .background(TeamFisColor.Surface1)
            .padding(SegmentTrackPadding),
    ) {
        // `maxWidth` 는 트랙 안쪽(padding 을 뺀) 폭이다
        val segmentWidth = maxWidth / labels.size
        val offset by animateDpAsState(
            targetValue = segmentWidth * selected,
            animationSpec = TeamFisMotion.base(),
            label = "segmentPill",
        )

        Box(
            Modifier
                .offset(x = offset)
                .width(segmentWidth)
                .fillMaxHeight()
                .background(TeamFisColor.Surface2, TeamFisRadius.card),
        )

        Row(Modifier.fillMaxSize()) {
            labels.forEachIndexed { index, label ->
                val interaction = remember(index) { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = { onSelect(index) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        style = TeamFisType.bodySm,
                        color = if (index == selected) {
                            TeamFisColor.TextPrimary
                        } else {
                            TeamFisColor.TextTertiary
                        },
                    )
                }
            }
        }
    }
}

/**
 * 여럿 중 하나 고르는 칩 무리 — 방문 경로 같은 것.
 *
 * 넘치면 다음 줄로 흘린다. 가로 스크롤로 만들면 뒤쪽 갈래가 숨어서
 * 고를 수 있는 것이 몇 개인지 모른다.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChoiceChips(
    labels: List<String>,
    selected: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
    ) {
        labels.forEachIndexed { index, label ->
            val isSelected = index == selected
            val interaction = remember(index) { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .height(TeamFisSize.chip)
                    .clip(TeamFisRadius.card)
                    .background(
                        if (isSelected) TeamFisColor.Brand else TeamFisColor.Surface1,
                    )
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        onClick = { onSelect(index) },
                    )
                    .padding(horizontal = TeamFisSpacing.md),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = TeamFisType.bodySm,
                    color = if (isSelected) TeamFisColor.TextPrimary else TeamFisColor.TextSecondary,
                )
            }
        }
    }
}

/** 켜고 끄는 줄 — 제목 + 설명 + 스위치. */
@Composable
fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .padding(horizontal = TeamFisSpacing.lg, vertical = TeamFisSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(title, style = TeamFisType.bodySm, color = TeamFisColor.TextPrimary)
            Text(description, style = TeamFisType.caption, color = TeamFisColor.TextTertiary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TeamFisColor.TextPrimary,
                checkedTrackColor = TeamFisColor.Brand,
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = TeamFisColor.TextTertiary,
                uncheckedTrackColor = TeamFisColor.Surface2,
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

/**
 * 화면 아래 고정 버튼.
 *
 * **필수 칸이 다 차야 빨갛게 찬다.** 덜 찼을 때 눌리지 않게 막는 대신
 * 눌리게 두고 **무엇이 비었는지 말해 준다** — 왜 안 되는지 모르는 것보다 낫다.
 */
@Composable
fun BottomActionButton(
    label: String,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TeamFisSize.actionButton)
            .clip(TeamFisRadius.card)
            .background(if (filled) TeamFisColor.Brand else TeamFisColor.Surface2)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = TeamFisType.titleSm,
            color = if (filled) TeamFisColor.TextPrimary else TeamFisColor.TextTertiary,
        )
    }
}

private val FieldVertical = 15.dp
private val FieldHeight = 52.dp
private val SegmentTrackPadding = 4.dp
