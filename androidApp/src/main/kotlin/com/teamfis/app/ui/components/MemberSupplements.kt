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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 영양제 목록 — 트레이너가 권한 것이 그대로 회원 화면에도 뜬다 (HiFIS 와 같은 자리).
 *
 * **줄에는 이름과 `얼마나 · 언제` 만 편다.** 다섯 칸을 다 늘어놓으면 줄 하나가
 * 카드만큼 커져서, 대여섯 개만 담아도 회원 상세가 영양제로 가득 찬다.
 * `왜?` 와 `기억하기` 는 눌러서 여는 자리에 다 있다.
 */
@Composable
fun SupplementList(
    supplements: List<Supplement>,
    onEdit: (Int) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .padding(TeamFisSpacing.lg),
    ) {
        if (supplements.isEmpty()) {
            Text(
                "챙겨 드시면 좋을 영양제를 담아 두세요",
                style = TeamFisType.bodySm,
                color = TeamFisColor.TextMuted,
            )
        } else {
            supplements.forEachIndexed { index, row ->
                if (index > 0) Spacer(Modifier.height(TeamFisSpacing.md))
                SupplementRow(row, onClick = { onEdit(index) })
            }
        }

        AddInlineButton(
            "영양제 추가",
            onClick = onAdd,
            modifier = Modifier.padding(top = TeamFisSpacing.md),
        )
    }
}

@Composable
private fun SupplementRow(row: Supplement, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TeamFisRadius.card)
            .background(TeamFisColor.Surface2)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = TeamFisSpacing.md, vertical = TeamFisSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(row.name, style = TeamFisType.bodySm, color = TeamFisColor.TextPrimary)
            if (row.summary.isNotBlank()) {
                Text(
                    row.summary,
                    style = TeamFisType.caption,
                    color = TeamFisColor.TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null, // 왼쪽 이름이 이름 역할을 한다
            tint = TeamFisColor.TextTertiary,
            modifier = Modifier.size(18.dp),
        )
    }
}

/**
 * 영양제 고르기 — 자주 쓰는 표에서 고른다.
 *
 * **고르면 네 칸이 함께 채워진다.** 매번 손으로 적으면 회원마다 말이 달라지고,
 * 그러면 회원이 트레이너마다 다른 안내를 받는다.
 */
@Composable
fun SupplementPickerDialog(
    onPick: (Supplement) -> Unit,
    onWriteMyself: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(TeamFisColor.Surface1, TeamFisRadius.card)
                .padding(TeamFisSpacing.xl),
        ) {
            Text("영양제 고르기", style = TeamFisType.titleSm, color = TeamFisColor.TextPrimary)
            Text(
                "고르면 얼마나 · 언제 · 왜가 함께 채워져요",
                style = TeamFisType.caption,
                color = TeamFisColor.TextTertiary,
                modifier = Modifier.padding(top = TeamFisSpacing.xs),
            )

            LazyColumn(
                modifier = Modifier
                    .padding(top = TeamFisSpacing.lg)
                    // 열여덟 줄이라 다 펴면 화면을 넘는다
                    .heightIn(max = PickerMaxHeight),
                verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
            ) {
                items(supplementPresets) { preset ->
                    val interaction = remember(preset.name) { MutableInteractionSource() }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(TeamFisRadius.card)
                            .background(TeamFisColor.Surface2)
                            .clickable(
                                interactionSource = interaction,
                                indication = null,
                                onClick = { onPick(preset) },
                            )
                            .padding(TeamFisSpacing.md),
                    ) {
                        Text(
                            preset.name,
                            style = TeamFisType.bodySm,
                            color = TeamFisColor.TextPrimary,
                        )
                        Text(
                            preset.reason,
                            style = TeamFisType.caption,
                            color = TeamFisColor.TextTertiary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = TeamFisSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                SessionButton("직접 적기", TeamFisColor.Surface2, TeamFisColor.TextSecondary) {
                    onWriteMyself()
                }
                SessionButton("닫기", TeamFisColor.Surface2, TeamFisColor.TextSecondary, onDismiss)
            }
        }
    }
}

/**
 * 영양제 담기 · 고치기 — 다섯 칸.
 *
 * **이름이 비면 저장이 안 찬다.** 브랜드 면에 흰 글씨를 두면 눌러도 아무 일이
 * 안 일어나는 버튼이 살아 있는 것처럼 보인다.
 */
@Composable
fun SupplementEditDialog(
    initial: Supplement,
    editing: Boolean,
    onSave: (Supplement) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var row by remember(initial) { mutableStateOf(initial) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(TeamFisColor.Surface1, TeamFisRadius.card)
                .padding(TeamFisSpacing.xl)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                if (editing) "영양제 고치기" else "영양제 담기",
                style = TeamFisType.titleSm,
                color = TeamFisColor.TextPrimary,
                modifier = Modifier.padding(bottom = TeamFisSpacing.lg),
            )

            FieldLabel("영양제")
            FormField(row.name, { row = row.copy(name = it) }, "오메가3")

            FieldLabel("얼마나?", Modifier.padding(top = TeamFisSpacing.md))
            FormField(row.dose, { row = row.copy(dose = it) }, "1000~3000mg")

            FieldLabel("언제?", Modifier.padding(top = TeamFisSpacing.md))
            FormField(row.timing, { row = row.copy(timing = it) }, "아침식후")

            FieldLabel("왜?", Modifier.padding(top = TeamFisSpacing.md))
            FormField(row.reason, { row = row.copy(reason = it) }, "성인병 예방, 염증완화", lines = 2)

            FieldLabel("기억하기", Modifier.padding(top = TeamFisSpacing.md))
            FormField(row.note, { row = row.copy(note = it) }, "식사 직후", lines = 2)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = TeamFisSpacing.xl),
                horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                if (editing) {
                    SessionButton("빼기", TeamFisColor.Surface2, TeamFisColor.Brand, onDelete)
                } else {
                    SessionButton("취소", TeamFisColor.Surface2, TeamFisColor.TextSecondary, onDismiss)
                }
                val filled = row.name.isNotBlank()
                SessionButton(
                    label = if (editing) "수정" else "담기",
                    background = if (filled) TeamFisColor.Brand else TeamFisColor.Surface2,
                    contentColor = if (filled) TeamFisColor.TextPrimary else TeamFisColor.TextTertiary,
                    onClick = { if (filled) onSave(row) },
                )
            }
        }
    }
}

private val PickerMaxHeight = 320.dp
