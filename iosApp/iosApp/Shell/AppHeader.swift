import SwiftUI

/// **상단 헤더** — 왼쪽 워드마크, 오른쪽 아이콘.
///
/// iOS 는 메시지·알림·마이 셋이다 (안드로이드는 앞에 검색이 하나 더 붙는다) —
/// 여기서 검색은 하단 유리 바가 맡는다.
/// 벡터는 MyFIS 와 같은 것을 쓴다 (아웃라인 1.5px).
///
/// **시스템 툴바(`.toolbar`)를 쓰지 않는다** — 화면이 자기 헤더를 직접 그려야
/// 헤더가 페이지와 함께 움직인다.
struct AppHeader: View {
    var onMessage: () -> Void = {}
    var onNotification: () -> Void = {}
    var onMy: () -> Void = {}

    var body: some View {
        HStack(spacing: 0) {
            Text("TeamFIS")
                .font(.system(size: 20, weight: .bold))
                .foregroundStyle(.white)

            Spacer(minLength: 0)

            // TODO: 메시지 화면이 붙으면 연결한다
            HeaderIcon("ic_header_message", "메시지", action: onMessage)
            HeaderIcon("ic_header_notification", "알림", action: onNotification)
            HeaderIcon("ic_header_my", "마이", action: onMy)
        }
        .frame(height: Header.height)
        .padding(.leading, Header.screenHorizontal)
        // 오른쪽은 터치 영역이 아이콘 밖으로 튀어나온 만큼 뺀다 (안드로이드와 같은 계산)
        .padding(.trailing, Header.screenHorizontal - (Header.touchTarget - Header.icon) / 2)
    }
}

/// 헤더의 아이콘 버튼. 아이콘은 24pt 지만 **터치 영역은 44pt** 로 넉넉히 잡는다.
struct HeaderIcon: View {
    let asset: String
    let label: String
    let action: () -> Void

    init(_ asset: String, _ label: String, action: @escaping () -> Void) {
        self.asset = asset
        self.label = label
        self.action = action
    }

    var body: some View {
        Button(action: action) {
            Image(asset)
                .renderingMode(.template)
                .resizable()
                .frame(width: Header.icon, height: Header.icon)
                .frame(width: Header.touchTarget, height: Header.touchTarget)
                .contentShape(Rectangle())
        }
        .foregroundStyle(.white)
        .accessibilityLabel(label)
    }
}

enum Header {
    static let height: CGFloat = 56
    static let screenHorizontal: CGFloat = 20
    static let touchTarget: CGFloat = 44
    static let icon: CGFloat = 24
}
