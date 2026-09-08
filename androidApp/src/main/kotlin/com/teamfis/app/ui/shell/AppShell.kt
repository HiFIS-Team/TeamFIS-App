package com.teamfis.app.ui.shell

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.teamfis.app.ui.components.Member
import com.teamfis.app.ui.components.ScheduleClass
import com.teamfis.app.ui.screens.MemberDetailScreen
import com.teamfis.app.ui.screens.MemberRegisterScreen
import com.teamfis.app.ui.screens.MemberScreen
import com.teamfis.app.ui.screens.ScheduleDetailScreen
import com.teamfis.app.ui.screens.ScheduleScreen
import com.teamfis.app.ui.screens.NotificationScreen
import com.teamfis.app.ui.screens.MemberPickScreen
import com.teamfis.app.ui.theme.TeamFisColor

/**
 * 앱의 뿌리.
 *
 * 탭 셸 위로 잎 화면이 **오른쪽에서 왼쪽으로 들어와 셸을 덮는다.**
 *
 * 셸은 움직이지 않는다 — 하단 탭 바가 같이 밀려 나갔다 돌아오면 그 왕복이 눈에 걸린다.
 * 덮개만 움직이면 돌아왔을 때 바가 원래 자리에 그대로 있다.
 * **바 가시성을 상태로 끄지 않는다** — 끄면 툭 사라지고 툭 생긴다.
 * (`NavHost` 기본 전환은 700ms 크로스페이드라 직접 지정한다)
 */
@Composable
fun AppShell() {
    val nav = rememberNavController()
    // 상세로 넘길 회원. NavHost 인자로 객체를 실어 보낼 수 없어 셸이 들고 있는다
    var openedMember by remember { mutableStateOf<Member?>(null) }
    // 상세로 넘길 수업. 회원과 같은 이유로 셸이 들고 있는다
    var openedClass by remember { mutableStateOf<ScheduleClass?>(null) }
    // 등록 화면이 고른 회원들 — 고르는 잎이 등록 잎 위에 또 얹히므로 셸이 들고 있는다
    var registerReferrer by remember { mutableStateOf<Member?>(null) }
    var registerRenewMember by remember { mutableStateOf<Member?>(null) }

    NavHost(
        navController = nav,
        startDestination = Route.SHELL,
        modifier = Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background),
        enterTransition = { slideInHorizontally(pushSpec) { it } },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { slideOutHorizontally(pushSpec) { it } },
    ) {
        composable(Route.SHELL) {
            TabShell(
                onMember = {
                    openedMember = it
                    nav.navigateOnce(Route.MEMBER_DETAIL)
                },
                onClass = {
                    openedClass = it
                    nav.navigateOnce(Route.SCHEDULE_DETAIL)
                },
                onNotification = { nav.navigateOnce(Route.NOTIFICATIONS) },
                onAddMember = {
                    // 지난번에 고른 회원이 남아 있으면 안 된다
                    registerReferrer = null
                    registerRenewMember = null
                    nav.navigateOnce(Route.MEMBER_REGISTER)
                },
            )
        }
        composable(Route.SCHEDULE_DETAIL) {
            // 뒤로 간 직후 한 프레임 동안 null 이 될 수 있어 방어한다
            openedClass?.let {
                ScheduleDetailScreen(item = it, onBack = { nav.popBackStack() })
            }
        }
        composable(Route.NOTIFICATIONS) {
            NotificationScreen(onBack = { nav.popBackStack() })
        }
        composable(Route.MEMBER_REGISTER) {
            MemberRegisterScreen(
                onBack = { nav.popBackStack() },
                referrer = registerReferrer,
                onPickReferrer = { nav.navigateOnce(Route.REFERRER_PICK) },
                onClearReferrer = { registerReferrer = null },
                renewMember = registerRenewMember,
                onPickRenewMember = { nav.navigateOnce(Route.RENEW_MEMBER_PICK) },
                onClearRenewMember = { registerRenewMember = null },
            )
        }
        composable(Route.REFERRER_PICK) {
            MemberPickScreen(
                title = "소개한 회원",
                description = "이 회원을 데려온 기존 회원을 골라주세요",
                onBack = { nav.popBackStack() },
                onPick = {
                    registerReferrer = it
                    nav.popBackStack()
                },
            )
        }
        composable(Route.RENEW_MEMBER_PICK) {
            MemberPickScreen(
                title = "재등록할 회원",
                description = "등록권을 하나 더 발급할 회원을 골라주세요",
                onBack = { nav.popBackStack() },
                onPick = {
                    registerRenewMember = it
                    nav.popBackStack()
                },
            )
        }
        composable(Route.MEMBER_DETAIL) {
            // 뒤로 간 직후 한 프레임 동안 null 이 될 수 있어 방어한다
            openedMember?.let {
                MemberDetailScreen(member = it, onBack = { nav.popBackStack() })
            }
        }
    }
}

/**
 * 탭 셸 — 화면 + 하단 탭 바.
 *
 * **헤더는 셸이 아니라 화면이 들고 있다** (iOS 와 같은 방식). 회원 상세처럼
 * 워드마크 대신 뒤로가기를 그려야 하는 화면이 있어서, 셸이 헤더를 고정하면
 * 그 화면이 헤더를 걷어낼 방법이 없다.
 *
 * 아직 안 만든 탭은 이름만 띄우는 자리 표시자를 둔다.
 */
@Composable
private fun TabShell(
    onMember: (Member) -> Unit,
    onClass: (ScheduleClass) -> Unit,
    onNotification: () -> Unit,
    onAddMember: () -> Unit,
) {
    // ⚠️ `remember` 면 안 된다. 잎이 덮는 동안 셸은 컴포지션에서 빠지므로
    // 그냥 기억하면 **돌아왔을 때 홈 탭으로 리셋된다** (2026-09-08 확인)
    var selected by rememberSaveable { mutableStateOf(BottomTab.Home) }

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
                BottomTab.Schedule -> ScheduleScreen(
                    onClass = onClass,
                    onNotification = onNotification,
                )
                BottomTab.Member -> MemberScreen(
                    onMember = onMember,
                    onNotification = onNotification,
                    onAddMember = onAddMember,
                )
                // 홈·수업은 아직 자리 표시자다 — 홈은 나머지가 다 찬 뒤에 짠다
                else -> Column(Modifier.fillMaxSize()) {
                    AppHeader(onNotification = onNotification)

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
