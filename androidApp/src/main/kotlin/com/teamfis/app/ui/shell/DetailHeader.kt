package com.teamfis.app.ui.shell

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSize
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 밀려 들어온 화면(잎 화면)의 상단 바.
 *
 * 셸의 [AppHeader] 와 높이(56)·좌우 여백을 맞춰 두 화면이 겹칠 때 줄이 흔들리지 않는다.
 * 워드마크 자리에는 화면 제목이 들어간다 — 잎 화면은 **자기가 어디인지 밝혀야** 한다.
 */
@Composable
fun DetailHeader(
    /** 제목을 본문에서 크게 다루는 화면은 `null` 로 비운다 (예: 회원 상세) */
    title: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    backDescription: String = "뒤로",
    /** 오른쪽 아이콘 버튼. 없으면 자리도 비운다 */
    @DrawableRes actionIcon: Int? = null,
    actionDescription: String = "",
    onAction: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TeamFisSize.header)
            // 아이콘의 눈에 보이는 끝이 화면 여백에 서도록, 터치 영역이 튀어나온 만큼 뺀다
            .padding(horizontal = TeamFisSpacing.screenHorizontal - (TouchTarget - Icon24) / 2),
        contentAlignment = Alignment.Center,
    ) {
        if (title != null) {
            Text(
                title,
                style = TeamFisType.titleSm,
                color = TeamFisColor.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                // 이름이 길어도 양쪽 버튼 밑으로 파고들지 않게 자리를 비워 둔다
                modifier = Modifier.padding(horizontal = TouchTarget),
            )
        }

        HeaderButton(
            icon = R.drawable.ic_chevron_left,
            description = backDescription,
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        )

        if (actionIcon != null) {
            HeaderButton(
                icon = actionIcon,
                description = actionDescription,
                onClick = onAction,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}

/** 아이콘은 24dp 지만 **터치 영역은 44dp** 로 넉넉히 잡는다 (셸 헤더와 같은 계산). */
@Composable
private fun HeaderButton(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(TouchTarget)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = TeamFisColor.TextPrimary,
            modifier = Modifier.size(Icon24),
        )
    }
}

private val TouchTarget = 44.dp
private val Icon24 = 24.dp
