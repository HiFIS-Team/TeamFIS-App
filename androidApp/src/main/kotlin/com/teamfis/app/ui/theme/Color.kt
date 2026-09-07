package com.teamfis.app.ui.theme

import androidx.compose.ui.graphics.Color

/** 색 토큰. 위젯에 색을 직접 적지 말고 여기 것을 쓴다. */
object TeamFisColor {
    /** 브랜드 레드 — 로고 마크에서 잰 값 (`assets/brand/logo.png` 안쪽 평균) */
    val Brand = Color(0xFFFC0B21)

    val Background = Color.Black
    /** 카드 판 — 검정 위에 한 겹 뜬 면 */
    val Surface1 = Color(0xFF16171B)
    val Surface2 = Color(0xFF292A2F)

    val TextPrimary = Color.White
    val TextSecondary = Color(0xFFBCC1CB)
    val TextTertiary = Color(0xFF9BA2AF)
    val TextMuted = Color(0x73FFFFFF) // 흰색 45%

    /** 토요일 파랑 · 일요일 빨강 — 한국 달력 관행 */
    val WeekendSaturday = Color(0xFF7DA8FF)
    val WeekendSunday = Color(0xFFFF6B6B)
}
