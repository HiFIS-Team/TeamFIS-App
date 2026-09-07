import SwiftUI
import SharedKit

/// 홈 — 헤더 밑에 캘린더. 아래 내용은 아직 없다.
struct HomeScreen: View {
    @State private var selected = Date()
    @State private var month = Date()
    @State private var expanded = false

    var body: some View {
        VStack(spacing: 0) {
            AppHeader()

            HomeCalendar(selected: $selected, month: $month, expanded: expanded)
            CalendarBar(expanded: expanded) {
                withAnimation(TeamFisMotion.slow) { expanded.toggle() }
            }

            Spacer()
        }
        .onChange(of: selected) { _, new in month = new }
    }
}

/// 캘린더 아래 한 줄 — `펼쳐보기`.
///
/// 펼치기는 **화살표가 뒤집히며** 캘린더가 그 달로 늘어난다.
private struct CalendarBar: View {
    let expanded: Bool
    let onToggle: () -> Void

    var body: some View {
        HStack(spacing: 0) {
            Button(action: onToggle) {
                HStack(spacing: 2) {
                    Text(expanded ? "접기" : "펼쳐보기")
                        .font(TeamFisFont.bodySm)
                    Image("ic_chevron_down")
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: 18, height: 18)
                        .rotationEffect(.degrees(expanded ? 180 : 0))
                        .animation(TeamFisMotion.base, value: expanded)
                }
                .foregroundStyle(TeamFisColor.textSecondary)
                .padding(.horizontal, TeamFisSpacing.sm)
                .frame(height: TeamFisSize.chip)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)

            Spacer(minLength: 0)
        }
        .padding(.horizontal, TeamFisSpacing.screenHorizontal - TeamFisSpacing.sm)
    }
}
