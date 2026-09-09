import SwiftUI

/// 수업.
///
/// 트레이너가 수업 뒤에 할 일은 **일지를 쓰는 것**과 **회원에게 사인을 받는 것**
/// 둘뿐이라 필터도 둘이다. 다만 **두 갈래가 서로 다른 것을 나열한다** —
///
/// - **일지** — 보유 회원 전체. 회원을 고르고 그 안에서 회차를 편다
///   (HiFIS 와 같은 길, 2026-09-08 대표 지시). 지난 회차를 다시 펴야 하는 일이라
///   밀린 것만 모아 두면 이미 쓴 일지를 찾을 수 없다
/// - **세션 사인** — 밀린 것만. 받아야 할 것이 남았는지가 전부라 회원을 거칠 이유가 없다
///
/// 목록이 길어지므로 필터 줄은 **위에 고정**한다 (회원 목록과 같은 이유).
struct ClassScreen: View {
    var onMember: (Member) -> Void = { _ in }
    var onSign: (ClassTodo) -> Void = { _ in }
    var onNotification: () -> Void = {}

    @State private var filter: ClassFilter = .log

    private var counts: [ClassFilter: Int] {
        [.log: Member.placeholder.count, .sign: Self.pendingSigns().count]
    }

    var body: some View {
        VStack(spacing: 0) {
            AppHeader(onNotification: onNotification)

            ClassFilterBar(selected: filter, counts: counts, onSelect: { filter = $0 })
                .padding(.top, TeamFisSpacing.sm)
                .padding(.bottom, TeamFisSpacing.md)

            switch filter {
            // 회원 줄은 회원 탭 것을 그대로 쓴다 — 같은 사람이 화면마다 다르게
            // 생기면 안 된다. 여기서는 누르면 상세가 아니라 그 회원의 일지로 간다
            case .log:
                ScrollView {
                    LazyVStack(spacing: 0) {
                        ForEach(Array(Member.placeholder.enumerated()), id: \.element.id) { index, member in
                            if index > 0 { MemberDivider() }
                            MemberRow(member: member) { onMember(member) }
                        }
                    }
                    .padding(.bottom, TeamFisSize.bottomBarClearance)
                }

            case .sign:
                let shown = Self.pendingSigns()
                if shown.isEmpty {
                    Spacer()
                    Text("받을 사인이 없어요")
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(TeamFisColor.textMuted)
                    Spacer()
                } else {
                    ScrollView {
                        VStack(spacing: TeamFisSpacing.md) {
                            ForEach(shown) { todo in
                                ClassCard(todo: todo, onSelect: { onSign(todo) })
                            }
                        }
                        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                        // 접힌 유리 바가 마지막 카드를 덮지 않게 (값의 근거는 토큰 주석에)
                        .padding(.bottom, TeamFisSize.bottomBarClearance)
                    }
                }
            }
        }
    }

    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 밀린 사인을 주면 통째로 걷어낸다.
    ///
    /// **끝난 수업에 일지까지 있는 것만** 온다 — 무엇을 했는지 적어야 회원이 그것을
    /// 확인해 줄 수 있어서다 (2026-09-08 대표 지시).
    static func pendingSigns() -> [ClassTodo] {
        func at(_ daysAgo: Int, _ hour: Int, _ minute: Int) -> Date {
            let calendar = TeamFisCalendar.calendar
            let day = calendar.date(byAdding: .day, value: -daysAgo, to: Date()) ?? Date()
            return calendar.date(bySettingHour: hour, minute: minute, second: 0, of: day) ?? day
        }

        return [
            ClassTodo(
                item: ScheduleClass(member: "000", at: at(1, 11, 0), minutes: 60,
                                    product: "얼리버드 10회", progress: "8/10회차", status: .done),
                parts: [.back, .arm, .cardio]
            ),
            ClassTodo(
                item: ScheduleClass(member: "000", at: at(3, 20, 0), minutes: 60,
                                    product: "PT 30회", progress: "24/30회차", status: .done),
                parts: [.chest, .shoulder]
            ),
        ]
    }
}
