package com.teamfis.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 캘린더와 오늘 수업 사이의 바로가기 두 칸 — 상담 일지 · 체형 분석.
 *
 * 둘이 **같은 폭**이다. 하나가 넓으면 둘 중 하나가 더 중요해 보인다.
 */
@Composable
fun ActionTiles(
    onConsultLog: () -> Unit = {},
    onBodyAnalysis: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
    ) {
        ActionTile(
            icon = R.drawable.ic_tab_log,
            title = "상담 일지",
            subtitle = "작성하기",
            onClick = onConsultLog,
        )
        ActionTile(
            icon = R.drawable.ic_analysis,
            title = "체형 분석",
            subtitle = "기록하기",
            onClick = onBodyAnalysis,
        )
    }
}

@Composable
private fun RowScope.ActionTile(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .weight(1f)
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(TeamFisSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null, // 옆 글자가 이름 역할을 한다
            tint = TeamFisColor.TextPrimary,
            modifier = Modifier.size(IconSize),
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = TeamFisType.titleSm, color = TeamFisColor.TextPrimary)
            Text(subtitle, style = TeamFisType.bodySm, color = TeamFisColor.TextTertiary)
        }
    }
}

private val IconSize = 24.dp
