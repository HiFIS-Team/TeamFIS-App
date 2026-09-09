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
/// 짜임새는 팀버핏 코치 앱을 참고했다 (2026-09-08 대표 지시) —
/// 큰 날짜 제목 → 주 달력 → 그날 수업 카드. 값은 TeamFIS 것(PT 1:1)으로 갈았다.
struct ScheduleScreen: View {
    var onClass: (ScheduleClass) -> Void = { _ in }
    var onNotification: () -> Void = {}
    var onMy: () -> Void = {}

    @State private var selected = Date()
    @State private var month = Date()
    @State private var expanded = false

    var body: some View {
        VStack(spacing: 0) {
            AppHeader(onNotification: onNotification, onMy: onMy)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    ScheduleCalendar(
                        selected: $selected,
                        month: $month,
                        expanded: expanded,
                        hasClass: Self.hasClass
                    )
                    .padding(.top, TeamFisSpacing.sm)

                    CalendarBar(expanded: expanded) {
                        withAnimation(TeamFisMotion.slow) { expanded.toggle() }
                    }
                    .padding(.top, TeamFisSpacing.xs)

                    let classes = Self.classes(on: selected)
                    if classes.isEmpty {
                        Text("일정이 없어요")
                            .font(TeamFisFont.bodySm)
                            .foregroundStyle(TeamFisColor.textMuted)
                            .frame(maxWidth: .infinity)
                            .padding(.top, TeamFisSpacing.xxxl)
                    } else {
                        VStack(spacing: TeamFisSpacing.md) {
                            ForEach(classes) { item in
                                ScheduleClassCard(item: item, onSelect: { onClass(item) })
                            }
                        }
                        .padding(.top, TeamFisSpacing.lg)
                        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                    }
                }
                // 접힌 유리 바가 마지막 줄을 덮지 않게 (값의 근거는 토큰 주석에)
                .padding(.bottom, TeamFisSize.bottomBarClearance)
            }
        }
        .onChange(of: selected) { _, new in month = new }
    }

    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 일정을 주면 통째로 걷어낸다.
    /// 오늘·내일·사흘 뒤에만 수업이 있는 것으로 둔다 — 점이 찍히는 날과 목록이 어긋나면 안 된다.
    static func hasClass(_ date: Date) -> Bool {
        let calendar = Calendar.current
        let today = Date()
        return [0, 1, 3].contains {
            guard let day = calendar.date(byAdding: .day, value: $0, to: today) else { return false }
            return calendar.isDate(day, inSameDayAs: date)
        }
    }

    static func classes(on date: Date) -> [ScheduleClass] {
        guard hasClass(date) else { return [] }
        return [
            ScheduleClass(member: "000", at: at(date, 10, 0), minutes: 60,
                          product: "얼리버드 20회", progress: "12/20회차", status: .done),
            ScheduleClass(member: "000", at: at(date, 14, 0), minutes: 60,
                          product: "PT 30회", progress: "12/30회차", status: .scheduled),
            ScheduleClass(member: "000", at: at(date, 16, 0), minutes: 60,
                          product: "PT 20회", progress: "3/20회차", status: .scheduled),
            ScheduleClass(member: "000", at: at(date, 18, 30), minutes: 60,
                          product: "얼리버드 10회", progress: "8/10회차", status: .scheduled),
        ]
    }

    private static func at(_ date: Date, _ hour: Int, _ minute: Int) -> Date {
        TeamFisCalendar.calendar.date(
            bySettingHour: hour, minute: minute, second: 0, of: date
        ) ?? date
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
