import SwiftUI

/// 밀려 들어온 화면(잎 화면)의 상단 바.
///
/// 셸의 `AppHeader` 와 높이(56)·좌우 여백을 맞춰 두 화면이 겹칠 때 줄이 흔들리지 않는다.
/// 워드마크 자리에는 화면 제목이 들어간다 — 잎 화면은 **자기가 어디인지 밝혀야** 한다.
struct DetailHeader: View {
    /// 제목을 본문에서 크게 다루는 화면은 `nil` 로 비운다 (예: 회원 상세)
    var title: String?
    let onBack: () -> Void
    var backLabel: String = "뒤로"
    /// 오른쪽 아이콘 버튼. 없으면 자리도 비운다
    var actionIcon: String?
    var actionLabel: String = ""
    var onAction: () -> Void = {}

    var body: some View {
        ZStack {
            if let title {
                Text(title)
                    .font(TeamFisFont.titleSm)
                    .foregroundStyle(TeamFisColor.textPrimary)
                    .lineLimit(1)
                    // 이름이 길어도 양쪽 버튼 밑으로 파고들지 않게 자리를 비워 둔다
                    .padding(.horizontal, Header.touchTarget)
            }

            HStack(spacing: 0) {
                HeaderIcon("ic_chevron_left", backLabel, action: onBack)
                Spacer(minLength: 0)
                if let actionIcon {
                    HeaderIcon(actionIcon, actionLabel, action: onAction)
                }
            }
        }
        .frame(height: Header.height)
        // 아이콘의 눈에 보이는 끝이 화면 여백에 서도록, 터치 영역이 튀어나온 만큼 뺀다
        .padding(.horizontal, Header.screenHorizontal - (Header.touchTarget - Header.icon) / 2)
    }
}
