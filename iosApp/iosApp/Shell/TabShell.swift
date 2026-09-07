import SwiftUI

/// 탭 셸 — 화면(자기 헤더 포함) + **하단 유리 탭 바**.
///
/// 바는 네이티브 `TabView` 를 쓴다. iOS 26 이 Liquid Glass 로 그리고
/// 선택 인디케이터·모션·스크롤 축소까지 전부 Apple 구현이다. 직접 그리지 않는다.
///
/// `Tab` API 는 iOS 18 부터라, 그 아래에서는 `.tabItem` 으로 떨어진다
/// (검색이 바 안에 다섯 번째 탭으로 들어간다).
struct TabShell: View {
    @State private var selection: TabItem = .home
    @State private var query = ""

    var body: some View {
        Group {
            if #available(iOS 18.0, *) {
                glassTabs
            } else {
                legacyTabs
            }
        }
        .tint(TeamFisColor.brand)
    }

    @available(iOS 18.0, *)
    private var glassTabs: some View {
        TabView(selection: $selection) {
            ForEach(TabItem.main, id: \.self) { tab in
                Tab(tab.label, image: iconName(tab), value: tab) {
                    TabScreen(tab: tab)
                }
            }

            // 바 밖에 따로 서는 자리. 누르면 **바가 검색 필드로 펼쳐진다** —
            // `.searchable` 을 붙여야 iOS 26 이 그 변형을 해 준다
            Tab(TabItem.search.label, image: iconName(.search), value: TabItem.search, role: .search) {
                NavigationStack {
                    SearchScreen(query: query)
                        .toolbar(.hidden, for: .navigationBar)
                        .searchable(text: $query, prompt: "회원, 일지, 세션 등")
                }
            }
        }
        // 탭 바 위 유리 줄 — 다음 수업. 값은 아직 자리 표시자다 (데이터가 붙으면 갈아끼운다)
        .bottomAccessory(NextClassBar(member: "000", time: "오후 2:00"))
        // 아래로 스크롤하면 접히고, 맨 위로 오면 다시 펴진다
        .minimizeTabBarOnScroll()
    }

    private var legacyTabs: some View {
        TabView(selection: $selection) {
            ForEach(TabItem.allCases, id: \.self) { tab in
                TabScreen(tab: tab)
                    .tabItem {
                        Image(iconName(tab)).accessibilityLabel(tab.label)
                    }
                    .tag(tab)
            }
        }
    }

    private func iconName(_ tab: TabItem) -> String {
        selection == tab ? tab.iconFilled : tab.icon
    }
}
