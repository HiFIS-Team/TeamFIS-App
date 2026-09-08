package com.teamfis.app.ui.shell

import androidx.annotation.DrawableRes
import com.teamfis.app.R

/**
 * 하단 탭 — **안드로이드는 넷**이다 (홈·일정·회원·수업).
 *
 * `수업` 하나가 **일지와 세션을 같이 맡는다** (2026-09-08 대표 지시).
 * 둘 다 수업에 붙는 기록이라 탭을 따로 둘 만큼 남남이 아니었다.
 * 그래서 남은 자리에 `일정` 이 들어왔다.
 *
 * iOS 는 여기에 검색이 하나 더 붙는데, 그건 탭이 아니라 iOS 26 의
 * `Tab(role: .search)` 로 바 밖에 따로 선다. 안드로이드에서 검색은 헤더에 있다.
 */
enum class BottomTab(
    val label: String,
    @DrawableRes val icon: Int,
    @DrawableRes val iconFilled: Int,
) {
    Home("홈", R.drawable.ic_tab_home, R.drawable.ic_tab_home_fill),
    Schedule("일정", R.drawable.ic_tab_schedule, R.drawable.ic_tab_schedule_fill),
    Member("회원", R.drawable.ic_tab_member, R.drawable.ic_tab_member_fill),
    Class("수업", R.drawable.ic_tab_class, R.drawable.ic_tab_class_fill),
}
