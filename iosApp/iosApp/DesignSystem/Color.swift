import SwiftUI

/// 색 토큰. 뷰에 색을 직접 적지 말고 여기 것을 쓴다.
enum TeamFisColor {
    /// 브랜드 레드 — 로고 마크에서 잰 값 (`assets/brand/logo.png` 안쪽 평균)
    static let brand = Color(red: 252 / 255, green: 11 / 255, blue: 33 / 255)

    static let background = Color.black
    static let textPrimary = Color.white
    static let textMuted = Color.white.opacity(0.45)
}
