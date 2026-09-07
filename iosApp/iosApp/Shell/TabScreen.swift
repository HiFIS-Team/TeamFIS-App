import SwiftUI
import SharedKit

/// 탭 하나의 자리 표시자 — 헤더 + 가운데 글자.
///
/// 화면이 정해지면 이 자리를 각 화면이 가져간다. **헤더는 화면이 들고 있다** —
/// 셸이 아니라 화면마다 헤더가 다르기 때문이다.
struct TabScreen: View {
    let tab: TabItem

    var body: some View {
        ZStack {
            // 유리 바가 콘텐츠 위에 떠 있으므로 배경은 아래까지 깐다
            Color.black.ignoresSafeArea()

            VStack(spacing: 0) {
                AppHeader()

                Spacer()
                Text(tab == .home ? Greeting().greet() : tab.label)
                    .foregroundStyle(.white)
                Spacer()
            }
        }
    }
}
