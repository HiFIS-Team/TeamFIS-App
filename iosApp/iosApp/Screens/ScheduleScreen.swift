import SwiftUI

/// 일정.
///
/// 달력은 원래 홈에 있었는데 **일정으로 옮겼다** (2026-09-08 대표 지시).
/// 홈은 다른 화면이 다 찬 뒤에 마지막으로 짠다.
///
/// ⚠️ **헤더만 고정이고 달력부터 아래는 전부 스크롤한다.**
/// `safeAreaInset` 이나 `Group` 으로 감싸면 하단 유리 바가 **끝까지 내렸을 때
/// 제멋대로 펴진다** (2026-09-07 에 홈에서 겪었다).
///
/// 고른 날의 수업 목록이 아직 없다 — 붙으면 달력 밑에 온다.
struct ScheduleScreen: View {
    var onNotification: () -> Void = {}

    @State private var selected = Date()
    @State private var month = Date()
    @State private var expanded = false

    var body: some View {
        VStack(spacing: 0) {
            AppHeader(onNotification: onNotification)

            ScrollView {
                VStack(spacing: 0) {
                    ScheduleCalendar(selected: $selected, month: $month, expanded: expanded)
                        .padding(.top, TeamFisSpacing.sm)
                    CalendarBar(expanded: expanded) {
                        withAnimation(TeamFisMotion.slow) { expanded.toggle() }
                    }
                    .padding(.top, TeamFisSpacing.xs)
                }
                // 접힌 유리 바가 마지막 줄을 덮지 않게 (값의 근거는 토큰 주석에)
                .padding(.bottom, TeamFisSize.bottomBarClearance)
            }
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
