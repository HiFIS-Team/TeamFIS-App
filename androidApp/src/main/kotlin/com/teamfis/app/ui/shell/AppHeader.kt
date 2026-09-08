package com.teamfis.app.ui.shell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamfis.app.R

/**
 * **상단 헤더** — 왼쪽 워드마크, 오른쪽 아이콘.
 *
 * 아이콘은 안드로이드가 넷(검색·메시지·알림·마이), iOS 가 셋(메시지·알림·마이)이다.
 * iOS 에 검색이 없는 것은 하단 유리 바가 검색을 맡기 때문이다.
 * 벡터는 MyFIS 와 같은 것을 쓴다 (아웃라인 1.5px).
 */
@Composable
fun AppHeader(
    onSearch: () -> Unit = {},
    onMessage: () -> Unit = {},
    onNotification: () -> Unit = {},
    onMy: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HEADER_HEIGHT)
            // 오른쪽은 화면 여백(20)에서 **터치 영역이 아이콘 밖으로 튀어나온 만큼**(10) 뺀다.
            // 그래야 아이콘의 눈에 보이는 끝이 왼쪽 워드마크와 같은 여백에 선다
            .padding(start = SCREEN_HORIZONTAL, end = SCREEN_HORIZONTAL - (TOUCH_TARGET - ICON) / 2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "TeamFIS",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.weight(1f))

        HeaderIcon(R.drawable.ic_header_search, "검색", onSearch)
        // TODO: 메시지 화면이 붙으면 연결한다
        HeaderIcon(R.drawable.ic_header_message, "메시지", onMessage)
        HeaderIcon(R.drawable.ic_header_notification, "알림", onNotification)
        HeaderIcon(R.drawable.ic_header_my, "마이", onMy)
    }
}

/** 헤더의 아이콘 버튼. 아이콘은 24dp 지만 **터치 영역은 44dp** 로 넉넉히 잡는다. */
@Composable
private fun HeaderIcon(icon: Int, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(TOUCH_TARGET)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = Color.White,
            modifier = Modifier.size(ICON),
        )
    }
}

private val HEADER_HEIGHT = 56.dp
private val SCREEN_HORIZONTAL = 20.dp
private val TOUCH_TARGET = 44.dp
private val ICON = 24.dp
