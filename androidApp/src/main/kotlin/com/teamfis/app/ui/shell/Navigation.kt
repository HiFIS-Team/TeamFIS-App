package com.teamfis.app.ui.shell

import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.teamfis.app.ui.theme.TeamFisMotion

/** 화면 경로. */
object Route {
    /** 탭 셸 (헤더 + 하단 바). 잎 화면은 이 위를 통째로 덮는다. */
    const val SHELL = "shell"

    /** 회원 상세 — 회원 목록의 한 줄을 누르면 들어온다 */
    const val MEMBER_DETAIL = "member_detail"
}

/**
 * 화면 전환 스펙 — `TeamFisMotion.slow` 와 같은 값(320ms).
 *
 * 시스템 "애니메이션 배율"이 0이면 Compose 가 알아서 즉시 전환한다 (MotionDurationScale).
 */
val pushSpec = tween<IntOffset>(durationMillis = 320, easing = TeamFisMotion.easing)

/**
 * 연타로 같은 화면이 두 번 쌓이는 걸 막는다.
 *
 * 전환 중에는 현재 항목이 `RESUMED` 가 아니다 — 그때 들어온 탭은 버린다.
 */
fun NavController.navigateOnce(route: String) {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(route)
    }
}
