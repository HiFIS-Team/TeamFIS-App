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

/**
 * 앱 셸 — 헤더 + 화면 + 하단 탭 바.
 *
 * 화면은 아직 없다. 탭마다 이름만 띄우는 자리 표시자를 둔다.
 */
@Composable
fun AppShell() {
    var selected by remember { mutableStateOf(BottomTab.Home) }

    Scaffold(
        containerColor = Color.Black,
        bottomBar = { BottomTabBar(selected, onSelect = { selected = it }) },
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
        ) {
            AppHeader()

            when (selected) {
                BottomTab.Home -> HomeScreen()
                // 나머지 탭은 아직 자리 표시자다
                else -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = selected.label)
                }
            }
        }
    }
}
