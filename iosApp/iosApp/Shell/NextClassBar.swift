import SwiftUI

/// 하단 탭 바 **위**에 붙는 다음 수업 줄 — 왼쪽 회원, 오른쪽 시작 시간.
///
/// **유리를 직접 그리지 않는다.** iOS 26 의 `tabViewBottomAccessory` 자리라
/// 재질·모서리·스크롤 반응이 전부 시스템 것이다 (애플 뮤직 미니 플레이어와 같은 자리).
struct NextClassBar: View {
    let member: String
    let time: String

    var body: some View {
        HStack(spacing: TeamFisSpacing.md) {
            Text("\(member) 회원님")
                .font(TeamFisFont.bodySm)
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
}
