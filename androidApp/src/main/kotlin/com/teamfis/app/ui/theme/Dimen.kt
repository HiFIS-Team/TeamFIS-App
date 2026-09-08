package com.teamfis.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** 간격 — 4pt 베이스 */
object TeamFisSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp

    /** 화면 좌우 여백 */
    val screenHorizontal = 20.dp
}

object TeamFisRadius {
    val card = RoundedCornerShape(4.dp)
    val full = RoundedCornerShape(percent = 50)
}

object TeamFisSize {
    val header = 56.dp
    val minTouchTarget = 44.dp

    /** 알약 칩 높이 — 펼쳐보기 같은 것 */
    val chip = 36.dp

    /** 목록 한 줄의 최소 높이 — 글이 짧아도 줄이 납작해지지 않게 */
    val listRowMin = 56.dp

    /** 세그먼트 바 높이 (신규/재등록 같은 모드 고르개) */
    val segment = 48.dp

    /** 화면 아래 고정 버튼 */
    val actionButton = 52.dp
}
