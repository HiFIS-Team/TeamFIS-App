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

            ScrollView {
                VStack(spacing: 0) {
                    HomeCalendar(selected: $selected, month: $month, expanded: expanded)
                    CalendarBar(expanded: expanded) {
                        withAnimation(TeamFisMotion.slow) { expanded.toggle() }
                    }

                    ActionTiles()
                        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                        .padding(.top, TeamFisSpacing.lg)

                    todayClasses
                }
            }
        }
        .onChange(of: selected) { _, new in month = new }
    }

    /// 오늘 수업 — **시간 순**으로 앞의 세 건만 보여준다.
    private var todayClasses: some View {
        VStack(alignment: .leading, spacing: TeamFisSpacing.md) {
            Text("오늘 수업")
                .font(TeamFisFont.titleMd)
                .foregroundStyle(TeamFisColor.textPrimary)

            ForEach(Self.placeholderClasses) { item in
                TodayClassCard(item: item)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
        .padding(.top, TeamFisSpacing.lg)
        .padding(.bottom, TeamFisSpacing.xl)
    }

    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 오늘 수업을 주면 통째로 걷어낸다.
    /// 시간 순으로 이미 정렬돼 있다.
    private static let placeholderClasses = [
        TodayClass(member: "000", time: "오후 2:00", detail: "PT 12/30회차"),
        TodayClass(member: "000", time: "오후 4:00", detail: "PT 3/20회차"),
        TodayClass(member: "000", time: "오후 6:30", detail: "PT 8/10회차"),
    ]
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
