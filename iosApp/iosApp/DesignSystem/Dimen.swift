import CoreGraphics

/// 간격 — 4pt 베이스. 안드로이드 `TeamFisSpacing` 과 값이 같아야 한다.
enum TeamFisSpacing {
    static let xs: CGFloat = 4
    static let sm: CGFloat = 8
    static let md: CGFloat = 12
    static let lg: CGFloat = 16
    static let xl: CGFloat = 20
    static let xxl: CGFloat = 24
    static let xxxl: CGFloat = 32

    /// 화면 좌우 여백
    static let screenHorizontal: CGFloat = 20
}

enum TeamFisRadius {
    static let card: CGFloat = 4
}

enum TeamFisSize {
    static let header: CGFloat = 56
    static let minTouchTarget: CGFloat = 44

    /// 알약 칩 높이 — 펼쳐보기 같은 것
    static let chip: CGFloat = 36
}
