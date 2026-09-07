import SwiftUI

/// 검색 화면 — **검색 필드는 우리가 그리지 않는다.**
///
/// iOS 26 은 `Tab(role: .search)` 의 내용에 `.searchable` 이 붙어 있으면
/// 하단 유리 바를 **검색 필드로 변형시킨다** (애플 뮤직과 같은 동작).
/// 필드·마이크·모션이 전부 시스템 것이라 우리가 만들 것이 없다.
struct SearchScreen: View {
    let query: String

    var body: some View {
        ZStack {
            TeamFisColor.background.ignoresSafeArea()

            VStack(spacing: 0) {
                // 헤더는 다른 탭과 같이 화면이 들고 있다
                AppHeader()

                Spacer()
                Text(query.isEmpty ? "검색" : "'\(query)' 결과 없음")
                    .foregroundStyle(TeamFisColor.textMuted)
                Spacer()
            }
        }
    }
}
