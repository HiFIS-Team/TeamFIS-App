package com.teamfis.app.ui.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.teamfis.app.ui.screens.HomeScreen
import com.teamfis.app.ui.screens.MemberScreen

/**
 * 앱 셸 — 화면 + 하단 탭 바.
 *
 * **헤더는 셸이 아니라 화면이 들고 있다** (iOS 와 같은 방식). 회원 상세처럼
 * 워드마크 대신 뒤로가기를 그려야 하는 화면이 있어서, 셸이 헤더를 고정하면
 * 그 화면이 헤더를 걷어낼 방법이 없다.
 *
 * 아직 안 만든 탭은 이름만 띄우는 자리 표시자를 둔다.
 */
@Composable
fun AppShell() {
    var selected by remember { mutableStateOf(BottomTab.Home) }

    Scaffold(
        containerColor = Color.Black,
        bottomBar = { BottomTabBar(selected, onSelect = { selected = it }) },
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
        ) {
            when (selected) {
                BottomTab.Home -> HomeScreen()
                BottomTab.Member -> MemberScreen()
                // 나머지 탭은 아직 자리 표시자다
                else -> Column(Modifier.fillMaxSize()) {
                    AppHeader()

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = selected.label)
                    }
                }
            }
        }
    }
}
