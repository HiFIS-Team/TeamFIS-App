package com.teamfis.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 다음 수업 띠 — 캘린더 바로 밑. 왼쪽 회원, 오른쪽 시작 시간.
 *
 * **화면 끝까지 붙는다** (좌우 여백 없음). 카드가 아니라 띠라서 안쪽 여백만 준다.
 * iOS 는 같은 내용을 탭 바 위 유리 줄(`NextClassBar`)로 띄운다 — 자리가 다르다.
 */
@Composable
fun NextClassBanner(
    member: String,
    time: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(BannerHeight)
            .background(
                Brush.horizontalGradient(
                    listOf(TeamFisColor.BrandGradientStart, TeamFisColor.BrandGradientEnd),
                ),
            )
            .padding(horizontal = TeamFisSpacing.screenHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null, // 옆 글자가 이름 역할을 한다
            tint = TeamFisColor.TextPrimary,
            modifier = Modifier
                .size(ClockIcon)
                .padding(end = 0.dp),
        )
        Spacer(Modifier.size(TeamFisSpacing.sm))

        Text(
            "$member 회원님",
            style = TeamFisType.titleSm,
            color = TeamFisColor.TextPrimary,
        )

        Spacer(Modifier.weight(1f))

        Text(
            time,
            // 시간은 자릿수가 바뀌어도 자리가 안 흔들려야 한다
            style = TeamFisType.titleSm.copy(fontFeatureSettings = "tnum"),
            color = TeamFisColor.TextPrimary,
        )
    }
}

private val BannerHeight = 52.dp
private val ClockIcon = 18.dp
