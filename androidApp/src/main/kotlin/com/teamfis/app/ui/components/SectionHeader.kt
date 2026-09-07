package com.teamfis.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 섹션 제목 한 줄 — 왼쪽 제목(+개수), 오른쪽 액션.
 *
 * 홈의 섹션들이 **같은 줄 모양**을 쓴다. 아래 내용은 섹션마다 다르게 생겼어도
 * 제목 줄까지 다르면 화면이 흩어져 보인다.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    /** 제목 옆 작은 숫자 (없으면 안 그린다) */
    count: Int? = null,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.screenHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = TeamFisType.titleMd, color = TeamFisColor.TextPrimary)

        if (count != null) {
            Text(
                count.toString(),
                style = TeamFisType.titleMd,
                color = TeamFisColor.TextTertiary,
                modifier = Modifier.padding(start = TeamFisSpacing.sm),
            )
        }

        Spacer(Modifier.weight(1f))

        if (actionLabel != null) {
            val interaction = remember { MutableInteractionSource() }
            Text(
                actionLabel,
                style = TeamFisType.bodySm,
                color = TeamFisColor.TextSecondary,
                modifier = Modifier.clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onAction,
                ),
            )
        }
    }
}
