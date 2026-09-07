import SwiftUI

/// 색 토큰. 뷰에 색을 직접 적지 말고 여기 것을 쓴다.
enum TeamFisColor {
    /// 브랜드 레드 — 로고 마크에서 잰 값 (`assets/brand/logo.png` 안쪽 평균)
    static let brand = Color(red: 252 / 255, green: 11 / 255, blue: 33 / 255)

    static let background = Color.black
    /// 카드 판 — 검정 위에 한 겹 뜬 면
    static let surface1 = Color(red: 22 / 255, green: 23 / 255, blue: 27 / 255)
    static let surface2 = Color(red: 41 / 255, green: 42 / 255, blue: 47 / 255)

    static let textPrimary = Color.white
    static let textSecondary = Color(red: 188 / 255, green: 193 / 255, blue: 203 / 255)
    static let textTertiary = Color(red: 155 / 255, green: 162 / 255, blue: 175 / 255)
    static let textMuted = Color.white.opacity(0.45)

    /// 토요일 파랑 · 일요일 빨강 — 한국 달력 관행
    static let weekendSaturday = Color(red: 125 / 255, green: 168 / 255, blue: 255 / 255)
    static let weekendSunday = Color(red: 255 / 255, green: 107 / 255, blue: 107 / 255)
}
