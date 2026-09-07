package com.teamfis.app.ui.shell

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.teamfis.app.ui.theme.TeamFisColor

/**
 * 하단 탭 바.
 *
 * **선택된 탭만 브랜드 레드**로 물들인다 (iOS 는 `.tint` 가 같은 일을 한다).
 * 나머지는 흐린 흰색이고, 아이콘은 선택된 자리만 안쪽이 찬 벌로 바뀐다.
 * 인디케이터(알약)는 끈다 — 색이 이미 선택을 말하고 있어 알약까지 두면 시끄럽다.
 */
@Composable
fun BottomTabBar(
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = TeamFisColor.Background,
    ) {
        BottomTab.entries.forEach { tab ->
            val isSelected = tab == selected
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(tab) },
                icon = {
                    Icon(
                        painter = painterResource(if (isSelected) tab.iconFilled else tab.icon),
                        contentDescription = tab.label,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TeamFisColor.Brand,
                    selectedTextColor = TeamFisColor.Brand,
                    unselectedIconColor = TeamFisColor.TextMuted,
                    unselectedTextColor = TeamFisColor.TextMuted,
                    indicatorColor = Color.Transparent,
                ),
            )
        }
    }
}
