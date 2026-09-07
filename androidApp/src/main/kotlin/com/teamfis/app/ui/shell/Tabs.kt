package com.teamfis.app.ui.shell

import androidx.annotation.DrawableRes
import com.teamfis.app.R

/**
 * 하단 탭 — **안드로이드는 넷**이다 (홈·회원·일지·세션).
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
    Member("회원", R.drawable.ic_tab_member, R.drawable.ic_tab_member_fill),
    Log("일지", R.drawable.ic_tab_log, R.drawable.ic_tab_log_fill),
    Session("세션", R.drawable.ic_tab_session, R.drawable.ic_tab_session_fill),
}
