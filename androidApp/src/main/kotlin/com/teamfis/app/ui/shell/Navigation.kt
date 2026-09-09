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

    /** 수업 상세 — 일정의 수업 카드를 누르면 들어온다 */
    const val SCHEDULE_DETAIL = "schedule_detail"

    /** 회원 일지 — 수업 탭의 일지 목록에서 회원 하나를 누르면 들어온다 */
    const val CLASS_MEMBER = "class_member"

    /** 일지 작성 — 회원 일지에서 회차 하나를 누르면 들어온다 */
    const val CLASS_LOG = "class_log"

    /** 세션 사인 — 수업 탭의 사인 목록에서 한 건을 누르면 들어온다 */
    const val CLASS_SIGN = "class_sign"

    /** 알림함 — 헤더의 종을 누르면 들어온다 */
    const val NOTIFICATIONS = "notifications"

    /** 회원 등록 — 회원 목록의 FAB 으로 들어온다 */
    const val MEMBER_REGISTER = "member_register"

    /** 소개한 회원 고르기 — 등록 화면 위에 한 겹 더 얹힌다 */
    const val REFERRER_PICK = "referrer_pick"

    /** 재등록할 회원 고르기 — 소개한 회원과 같은 화면을 쓴다 */
    const val RENEW_MEMBER_PICK = "renew_member_pick"
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
