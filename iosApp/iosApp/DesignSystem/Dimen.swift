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

    /// 목록 한 줄의 최소 높이 — 글이 짧아도 줄이 납작해지지 않게
    static let listRowMin: CGFloat = 56

    /// 스크롤 맨 아래에 두는 여백 — **접힌 하단 유리 바가 마지막 줄을 덮지 않을 만큼**.
    /// 시스템이 잡아 주는 여백은 펴짐 기준이라, 접히며 줄어드는 만큼(~60)을 더해 둔다.
    ///
    /// **줄이지 말 것** — 줄이면 끝까지 내렸을 때 마지막 줄이 바에 가린다 (2026-09-07).
    /// 안드로이드에는 없는 값이다 (거기 탭 바는 콘텐츠 위에 안 뜬다).
    static let bottomBarClearance: CGFloat = 88
}
