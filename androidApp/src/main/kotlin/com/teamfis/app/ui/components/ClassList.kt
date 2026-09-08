package com.teamfis.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teamfis.app.ui.theme.TeamFisSpacing

/**
 * 수업 탭이 다루는 두 가지 일.
 *
 * 수업이 끝나면 트레이너가 할 일은 **일지를 쓰는 것**과 **회원에게 사인을 받는 것**
 * 둘뿐이다. 탭을 그 둘로 가른다.
 */
enum class ClassFilter(val label: String) {
    Log("일지"),
    Sign("세션 사인"),
}

/**
 * 수업 필터 — 일지 · 세션 사인.
 *
 * **회원 필터와 달리 하나는 늘 골라져 있다.** 거기서는 안 고른 상태가 곧 전체지만,
 * 여기서는 성격이 다른 두 목록이라 합쳐 놓으면 그 줄이 왜 떠 있는지 알 수 없다.
 *
 * 칩에 달린 숫자가 **남은 개수**다 — 고르지 않고도 어느 쪽이 밀렸는지 보인다.
 */
@Composable
fun ClassFilterBar(
    selected: ClassFilter,
    counts: Map<ClassFilter, Int>,
    onSelect: (ClassFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
    ) {
        ClassFilter.entries.forEach { filter ->
            CountChip(
                label = filter.label,
                count = counts[filter] ?: 0,
                selected = selected == filter,
                onClick = { onSelect(filter) },
            )
        }
    }
}
