package com.teamfis.app.ui.theme

import androidx.compose.ui.graphics.Color

/** 색 토큰. 위젯에 색을 직접 적지 말고 여기 것을 쓴다. */
object TeamFisColor {
    /** 브랜드 레드 — 로고 마크에서 잰 값 (`assets/brand/logo.png` 안쪽 평균) */
    val Brand = Color(0xFFFC0B21)

    /**
     * 큰 면·띠에 쓰는 브랜드 그라디언트 (왼쪽 → 오른쪽).
     *
     * 로고 마크 안에 **실제로 있는 두 극단**을 끝에 놓았다 — 가장 밝은 자리(`#FD4259`)와
     * 가장 깊은 자리(`#FB0209`). 평균끼리 이으면 차이가 1% 라 단색으로 보인다.
     */
    val BrandGradientStart = Color(0xFFFD4259)
    val BrandGradientEnd = Color(0xFFFB0209)

    val Background = Color.Black
    /** 카드 판 — 검정 위에 한 겹 뜬 면 */
    val Surface1 = Color(0xFF16171B)
    val Surface2 = Color(0xFF292A2F)

    val TextPrimary = Color.White
    val TextSecondary = Color(0xFFBCC1CB)
    val TextTertiary = Color(0xFF9BA2AF)
    val TextMuted = Color(0x73FFFFFF) // 흰색 45%

    /** 줄 구분선 — 면을 안 깔고 줄만 나눌 때 */
    val Divider = Color(0x1AFFFFFF) // 흰색 10%

    /**
     * 앞으로 일어날 일 — `수업예정` 배지.
     *
     * 브랜드 레드는 **지금 눌러야 할 것**(완료 버튼·만료 회원)이 가져간다.
     * 예정은 누를 것이 아니라 알려 주는 것이라 색을 갈라야 한다.
     */
    val Scheduled = Color(0xFF2E9E5B)

    /** 토요일 파랑 · 일요일 빨강 — 한국 달력 관행 */
    val WeekendSaturday = Color(0xFF7DA8FF)
    val WeekendSunday = Color(0xFFFF6B6B)
}
