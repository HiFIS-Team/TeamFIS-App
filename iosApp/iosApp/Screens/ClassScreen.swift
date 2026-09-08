import SwiftUI

/// 수업.
///
/// **끝난 수업 중 아직 안 닫힌 것을 모아 두는 자리다.** 일지를 안 썼거나 사인을
/// 못 받았거나 — 트레이너가 수업 뒤에 할 일은 그 둘뿐이라 필터도 둘이다.
///
/// 카드는 **일정과 같은 것**을 쓴다 (2026-09-08 대표 지시). 같은 수업 한 건이
/// 화면마다 다르게 생기면 안 된다. 다만 여기는 여러 날이 섞이므로 **날짜 한 줄이 더 붙는다** —
/// 일정은 하루치만 세워 날짜가 필요 없었다.
///
/// 목록이 길어지므로 필터 줄은 **위에 고정**한다 (회원 목록과 같은 이유).
///
/// 카드를 누르면 수업 상세로 간다 — 일지와 사인이 거기 있다.
struct ClassScreen: View {
    var onClass: (ScheduleClass) -> Void = { _ in }
    var onNotification: () -> Void = {}

    @State private var filter: ClassFilter = .log

    private var counts: [ClassFilter: Int] {
        Dictionary(uniqueKeysWithValues: ClassFilter.allCases.map { ($0, Self.pending($0).count) })
    }

    var body: some View {
        VStack(spacing: 0) {
            AppHeader(onNotification: onNotification)

            ClassFilterBar(selected: filter, counts: counts, onSelect: { filter = $0 })
                .padding(.top, TeamFisSpacing.sm)
                .padding(.bottom, TeamFisSpacing.md)

            let shown = Self.pending(filter)
            if shown.isEmpty {
                Spacer()
                Text("밀린 수업이 없어요")
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textMuted)
                Spacer()
            } else {
                ScrollView {
                    VStack(spacing: TeamFisSpacing.md) {
                        ForEach(shown) { item in
                            ScheduleClassCard(
                                item: item,
                                onSelect: { onClass(item) },
                                date: dayTitle(item.at)
                            )
                        }
                    }
                    .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                    // 접힌 유리 바가 마지막 카드를 덮지 않게 (값의 근거는 토큰 주석에)
                    .padding(.bottom, TeamFisSize.bottomBarClearance)
                }
            }
        }
    }

    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 밀린 수업을 주면 통째로 걷어낸다.
    ///
    /// 둘 다 **끝난 수업**이다 — 예정은 아직 쓸 것이 없고 노쇼는 한 게 없다.
    static func pending(_ filter: ClassFilter) -> [ScheduleClass] {
        func at(_ daysAgo: Int, _ hour: Int, _ minute: Int) -> Date {
            let calendar = TeamFisCalendar.calendar
            let day = calendar.date(byAdding: .day, value: -daysAgo, to: Date()) ?? Date()
            return calendar.date(bySettingHour: hour, minute: minute, second: 0, of: day) ?? day
        }

        switch filter {
        case .log:
            return [
                ScheduleClass(member: "000", at: at(0, 10, 0), minutes: 60,
                              product: "얼리버드 20회", progress: "12/20회차", status: .done),
                ScheduleClass(member: "000", at: at(1, 19, 30), minutes: 60,
                              product: "PT 30회", progress: "12/30회차", status: .done),
                ScheduleClass(member: "000", at: at(2, 14, 0), minutes: 60,
                              product: "PT 20회", progress: "3/20회차", status: .done),
            ]
        case .sign:
            return [
                ScheduleClass(member: "000", at: at(1, 11, 0), minutes: 60,
                              product: "얼리버드 10회", progress: "8/10회차", status: .done),
                ScheduleClass(member: "000", at: at(3, 20, 0), minutes: 60,
                              product: "PT 30회", progress: "24/30회차", status: .done),
            ]
        }
    }
}
