package com.teamfis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 회원 상세의 구역 머리 — `운동을 하는 이유` · `영양제`.
 *
 * 회원 상세는 여태 구역 없이 이어 붙었는데, 성격이 다른 덩어리가 셋(등록권 · 이유 ·
 * 영양제)이 되면서 어디서 끊기는지가 필요해졌다.
 */
@Composable
fun MemberSectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        style = TeamFisType.titleSm,
        color = TeamFisColor.TextPrimary,
        modifier = modifier.padding(bottom = TeamFisSpacing.md),
    )
}

/**
 * 운동을 하는 이유 — 번호를 매겨 적는다 (HiFIS 에서 가져왔다).
 *
 * **틀을 안 씌운다.** 회원이 말한 것을 그대로 옮겨 두는 자리라 목표 체중이든
 * `결혼식` 이든 한 줄씩 적히면 된다. 갈래를 만들어 고르게 하면 회원이 한 말이 사라진다.
 *
 * **회차가 아니라 사람에 붙는 값이다** — 일지에 두면 회차마다 다시 적게 된다.
 */
@Composable
fun MemberGoalList(
    goals: List<String>,
    onChange: (Int, String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .padding(TeamFisSpacing.lg),
    ) {
        goals.forEachIndexed { index, goal ->
            if (index > 0) Spacer(Modifier.height(TeamFisSpacing.sm))
            GoalRow(
                index = index,
                value = goal,
                onValueChange = { onChange(index, it) },
                onRemove = { onRemove(index) },
            )
        }

        AddInlineButton(
            "이유 추가",
            onClick = onAdd,
            modifier = Modifier.padding(top = TeamFisSpacing.md),
        )
    }
}

@Composable
private fun GoalRow(
    index: Int,
    value: String,
    onValueChange: (String) -> Unit,
    onRemove: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(GoalIndexSize)
                .clip(TeamFisRadius.card)
                .background(TeamFisColor.Surface2),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "${index + 1}",
                style = TeamFisType.caption.copy(fontFeatureSettings = "tnum"),
                color = TeamFisColor.TextTertiary,
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TeamFisType.bodySm.copy(color = TeamFisColor.TextPrimary),
            cursorBrush = SolidColor(TeamFisColor.Brand),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .padding(start = TeamFisSpacing.sm),
            decorationBox = { inner ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(GoalFieldHeight)
                        .clip(TeamFisRadius.card)
                        .background(TeamFisColor.Surface2)
                        .padding(horizontal = TeamFisSpacing.md),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        Text(
                            "예) 결혼식까지 -5kg",
                            style = TeamFisType.bodySm,
                            color = TeamFisColor.TextMuted,
                        )
                    }
                    inner()
                }
            },
        )

        val interaction = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .size(GoalRemoveSize)
                .clickable(interactionSource = interaction, indication = null, onClick = onRemove),
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
}

/**
 * 판 **안쪽**에 붙는 추가 줄 — `이유 추가` · `영양제 추가`.
 *
 * 일지 폼의 [AddRowButton] 은 표 **밖**에 서서 제 판이 필요했지만, 여기는 이미
 * 판 안이라 또 한 겹 깔면 판 속에 판이 된다. 글자만 브랜드 색으로 둔다.
 */
@Composable
fun AddInlineButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .clip(TeamFisRadius.card)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(vertical = TeamFisSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.xs),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = null, // 옆 글자가 이름 역할을 한다
            tint = TeamFisColor.Brand,
            modifier = Modifier.size(16.dp),
        )
        Text(label, style = TeamFisType.bodySm, color = TeamFisColor.Brand)
    }
}

private val GoalIndexSize = 20.dp
private val GoalFieldHeight = 40.dp
private val GoalRemoveSize = 28.dp
