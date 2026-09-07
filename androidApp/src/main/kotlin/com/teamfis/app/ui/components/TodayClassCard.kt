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

/** 오늘 수업 한 건. 데이터가 붙기 전까지는 화면에서 만들어 넣는다. */
data class TodayClass(
    val member: String,
    val time: String,
    val detail: String,
)

/**
 * 오늘 수업 카드 — 위에 회원 이름과 더보기, 아래에 시간·회차.
 *
 * 이름이 카드에서 제일 큰 글자다. 시간·회차는 그 밑에 명암을 낮춰 깐다.
 */
@Composable
fun TodayClassCard(
    item: TodayClass,
    modifier: Modifier = Modifier,
    onMore: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TeamFisColor.Surface1, TeamFisRadius.card)
            .padding(TeamFisSpacing.lg),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                item.member,
                style = TeamFisType.titleLg,
                color = TeamFisColor.TextPrimary,
                modifier = Modifier.weight(1f),
            )

            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    // 아이콘은 20 이지만 누를 자리는 넉넉히 잡는다
                    .size(MoreTouchTarget)
                    // 오른쪽·위로 붙여 카드 모서리와 간격을 맞춘다
                    .padding(0.dp)
                    .clickable(interactionSource = interaction, indication = null, onClick = onMore),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = "더보기",
                    tint = TeamFisColor.TextTertiary,
                    modifier = Modifier.size(MoreIcon),
                )
            }
        }

        // 이름 줄과 아래 정보 사이를 벌려 카드에 숨통을 준다
        Spacer(Modifier.height(TeamFisSpacing.xxl))

        Column(verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.xs)) {
            Text(
                item.time,
                // 시간은 자릿수가 바뀌어도 줄이 안 흔들려야 한다
                style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
                color = TeamFisColor.TextSecondary,
            )
            Text(
                item.detail,
                style = TeamFisType.caption,
                color = TeamFisColor.TextTertiary,
            )
        }
    }
}

private val MoreIcon = 20.dp
private val MoreTouchTarget = 28.dp
