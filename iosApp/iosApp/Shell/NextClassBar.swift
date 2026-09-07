import SwiftUI

/// 하단 탭 바 **위**에 붙는 다음 수업 줄 — 왼쪽 회원, 오른쪽 시작 시간.
///
/// **유리를 직접 그리지 않는다.** iOS 26 의 `tabViewBottomAccessory` 자리라
/// 재질·모서리·스크롤 반응이 전부 시스템 것이다 (애플 뮤직 미니 플레이어와 같은 자리).
struct NextClassBar: View {
    let member: String
    let time: String

    private let clockIcon: CGFloat = 18

    var body: some View {
        HStack(spacing: TeamFisSpacing.sm) {
            Image("ic_clock")
                .renderingMode(.template)
                .resizable()
                .frame(width: clockIcon, height: clockIcon)
                .foregroundStyle(TeamFisColor.textPrimary)

            Text("\(member) 회원님")
                // 오른쪽 시간과 **같은 크기**로 맞춘다 — 한 줄 안에서 둘이 짝이다
                .font(TeamFisFont.titleSm)
                .foregroundStyle(TeamFisColor.textPrimary)
                .lineLimit(1)

            Spacer(minLength: 0)

            Text(time)
                // 시간은 자릿수가 바뀌어도 자리가 안 흔들려야 한다
                .font(TeamFisFont.titleSm.monospacedDigit())
                .foregroundStyle(TeamFisColor.textPrimary)
        }
        .padding(.horizontal, TeamFisSpacing.lg)
    }
}

extension View {
    /// 탭 바 위 유리 자리. **iOS 26 부터만 있다** — 그 아래에서는 그냥 안 붙는다.
    @ViewBuilder
    func bottomAccessory(_ content: some View) -> some View {
        if #available(iOS 26.0, *) {
            self.tabViewBottomAccessory { content }
        } else {
            self
        }
    }

    /// 아래로 스크롤하면 탭 바가 **접히고**, 맨 위로 돌아오면 다시 펴진다
    /// (애플 뮤직과 같은 동작). 접히는 모양·모션은 전부 시스템 것이다.
    ///
    /// **iOS 26 부터만 있다** — 그 아래에서는 바가 늘 펴져 있다.
    @ViewBuilder
    func minimizeTabBarOnScroll() -> some View {
        if #available(iOS 26.0, *) {
            self.tabBarMinimizeBehavior(.onScrollDown)
        } else {
            self
        }
    }
}
