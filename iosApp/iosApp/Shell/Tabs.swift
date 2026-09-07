import Foundation

/// 하단 탭.
///
/// **네 개는 바 안에, 검색은 바 밖에 선다** — iOS 26 은 `role: .search` 인 탭을
/// 유리 바에서 떼어내 옆에 동그란 버튼으로 그린다 (Apple Music 과 같은 모양).
/// 안드로이드에는 검색 탭이 없다. 거기선 검색이 헤더에 있다.
enum TabItem: Hashable, CaseIterable {
    case home, member, log, session, search

    /// 유리 바 안에 서는 넷
    static let main: [TabItem] = [.home, .member, .log, .session]

    var label: String {
        switch self {
        case .home: "홈"
        case .member: "회원"
        case .log: "일지"
        case .session: "세션"
        case .search: "검색"
        }
    }

    var icon: String {
        switch self {
        case .home: "ic_tab_home"
        case .member: "ic_tab_member"
        case .log: "ic_tab_log"
        case .session: "ic_tab_session"
        case .search: "ic_tab_search"
        }
    }

    /// 선택된 자리는 **안쪽이 찬 벌**로 바꾼다. 검색은 바 밖에 있어 채움 벌이 없다.
    var iconFilled: String {
        self == .search ? icon : "\(icon)_fill"
    }
}
