import SwiftUI
import SharedKit

/// 탭 하나의 자리 표시자 — 헤더 + 가운데 글자.
///
/// 화면이 정해지면 이 자리를 각 화면이 가져간다. **헤더는 화면이 들고 있다** —
/// 셸이 아니라 화면마다 헤더가 다르기 때문이다.
struct TabScreen: View {
    let tab: TabItem

    var body: some View {
        Group {
            if tab == .home {
                HomeScreen()
            } else {
                VStack(spacing: 0) {
                    AppHeader()

                    Spacer()
                    Text(tab.label)
                        .foregroundStyle(TeamFisColor.textPrimary)
                    Spacer()
                }
            }
        }
        // 배경만 안전영역 밖까지 깐다. **화면 자체는 안전영역을 지켜야**
        // 하단 유리 바 높이만큼 스크롤 끝이 밀려 콘텐츠가 안 가린다
        .background(TeamFisColor.background.ignoresSafeArea())
    }
}
