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

    /// 줄 구분선 — 면을 안 깔고 줄만 나눌 때
    static let divider = Color.white.opacity(0.10)

    /// 앞으로 일어날 일 — `수업예정` 배지.
    ///
    /// 브랜드 레드는 **지금 눌러야 할 것**(완료 버튼·만료 회원)이 가져간다.
    /// 예정은 누를 것이 아니라 알려 주는 것이라 색을 갈라야 한다.
    static let scheduled = Color(red: 46 / 255, green: 158 / 255, blue: 91 / 255)

    /// 갈래 색 — 알림 종류처럼 **여러 갈래를 색으로 가르는** 자리에만 쓴다.
    ///
    /// 브랜드 레드는 여기 끼지 않는다. 액션 색이 갈래 하나를 맡으면
    /// 그 갈래만 눌러야 할 것처럼 보인다.
    static let categoryBlue = Color(red: 125 / 255, green: 168 / 255, blue: 255 / 255)
    static let categoryGreen = Color(red: 74 / 255, green: 222 / 255, blue: 128 / 255)
    static let categoryViolet = Color(red: 167 / 255, green: 139 / 255, blue: 250 / 255)
    static let categoryCoral = Color(red: 255 / 255, green: 138 / 255, blue: 107 / 255)
    static let categoryGray = Color(red: 155 / 255, green: 162 / 255, blue: 175 / 255)

    /// 토요일 파랑 · 일요일 빨강 — 한국 달력 관행
    static let weekendSaturday = Color(red: 125 / 255, green: 168 / 255, blue: 255 / 255)
    static let weekendSunday = Color(red: 255 / 255, green: 107 / 255, blue: 107 / 255)
}
