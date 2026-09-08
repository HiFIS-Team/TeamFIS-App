import SwiftUI
import SharedKit

/// 탭 하나 — 만든 화면이 있으면 그 화면, 없으면 헤더 + 가운데 글자.
///
/// **헤더는 화면이 들고 있다** — 셸이 아니라 화면마다 헤더가 다르기 때문이다.
struct TabScreen: View {
    let tab: TabItem
    let open: (Route) -> Void

    var body: some View {
        ZStack {
            // 유리 바가 콘텐츠 위에 떠 있으므로 배경은 아래까지 깐다
            Color.black.ignoresSafeArea()

            switch tab {
            case .schedule:
                ScheduleScreen(
                    onClass: { open(.scheduleDetail($0)) },
                    onNotification: { open(.notifications) }
                )
            case .lesson:
                ClassScreen(
                    onClass: { open(.scheduleDetail($0)) },
                    onSign: { open(.classSign($0)) },
                    onNotification: { open(.notifications) }
                )
            case .member:
                MemberScreen(
                    onMember: { open(.memberDetail($0)) },
                    onNotification: { open(.notifications) },
                    onAddMember: { open(.memberRegister) }
                )
            default:
                VStack(spacing: 0) {
                    AppHeader(onNotification: { open(.notifications) })

                    Spacer()
                    Text(tab.label)
                        .foregroundStyle(.white)
                    Spacer()
                }
            }
        }
    }
}
