import Foundation

/// 하단 탭.
///
/// `수업` 하나가 **일지와 세션을 같이 맡는다** (2026-09-08 대표 지시).
/// 둘 다 수업에 붙는 기록이라 탭을 따로 둘 만큼 남남이 아니었다.
/// 그래서 남은 자리에 `일정` 이 들어왔다.
///
/// **네 개는 바 안에, 검색은 바 밖에 선다** — iOS 26 은 `role: .search` 인 탭을
/// 유리 바에서 떼어내 옆에 동그란 버튼으로 그린다 (Apple Music 과 같은 모양).
/// 안드로이드에는 검색 탭이 없다. 거기선 검색이 헤더에 있다.
enum TabItem: Hashable, CaseIterable {
    case home, schedule, member, lesson, search

    /// 유리 바 안에 서는 넷
    static let main: [TabItem] = [.home, .schedule, .member, .lesson]

    var label: String {
        switch self {
        case .home: "홈"
        case .schedule: "일정"
        case .member: "회원"
        case .lesson: "수업"
        case .search: "검색"
        }
    }

    var icon: String {
        switch self {
        case .home: "ic_tab_home"
        case .schedule: "ic_tab_schedule"
        case .member: "ic_tab_member"
        case .lesson: "ic_tab_class"
        case .search: "ic_tab_search"
        }
    }

    /// 선택된 자리는 **안쪽이 찬 벌**로 바꾼다. 검색은 바 밖에 있어 채움 벌이 없다.
    var iconFilled: String {
        self == .search ? icon : "\(icon)_fill"
    }
}
