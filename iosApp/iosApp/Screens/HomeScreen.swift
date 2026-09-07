import SwiftUI
import SharedKit

/// 홈.
///
/// 섹션 순서는 **놓치면 손해 보는 순서**다 — 오늘 할 일(오늘 수업) → 챙길 사람(챙길 회원)
/// → 밀린 일(미작성 일지) → 돌아보는 숫자(이번 달).
///
/// 섹션마다 **아래 내용의 생김새가 다르다.** 같은 카드가 끝까지 내려오면 화면이 지루하고,
/// 어디가 어디인지 훑어서 못 찾는다. 제목 줄만 `SectionHeader` 로 통일한다.
struct HomeScreen: View {
    @State private var selected = Date()
    @State private var month = Date()
    @State private var expanded = false

    var body: some View {
        // **헤더만 고정이고 달력부터 아래는 전부 스크롤한다** (MyFIS 홈과 같은 구조).
        //
        // ⚠️ 이 모양을 바꾸지 않는다. `safeAreaInset` 이나 `Group` 으로 감쌌더니
        // 하단 유리 바가 **끝까지 내렸을 때 제멋대로 펴졌다** (2026-09-07).
        VStack(spacing: 0) {
            AppHeader()

            ScrollView {
                VStack(spacing: 0) {
                    HomeCalendar(selected: $selected, month: $month, expanded: expanded)
                        .padding(.top, TeamFisSpacing.sm)
                    CalendarBar(expanded: expanded) {
                        withAnimation(TeamFisMotion.slow) { expanded.toggle() }
                    }
                    .padding(.top, TeamFisSpacing.xs)

                    ActionTiles()
                        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                        .padding(.top, TeamFisSpacing.lg)

                    // 놓치면 손해 보는 순서 — 오늘 할 일 → 챙길 사람 → 밀린 일 → 돌아보는 숫자
                    todayClasses
                    careMembers
                    pendingLogs
                    monthSummary
                }
                // 접힌 유리 바가 마지막 판을 덮지 않게 (값의 근거는 토큰 주석에)
                .padding(.bottom, TeamFisSize.bottomBarClearance)
            }
        }
        .onChange(of: selected) { _, new in month = new }
    }

    /// 오늘 수업 — **시간 순**으로 앞의 세 건만 보여준다. 큰 카드가 세로로 쌓인다.
    private var todayClasses: some View {
        section(title: "오늘 수업", actionLabel: "전체보기") {
            VStack(spacing: TeamFisSpacing.md) {
                ForEach(Self.placeholderClasses) { item in
                    TodayClassCard(item: item)
                }
            }
            .padding(.horizontal, TeamFisSpacing.screenHorizontal)
        }
    }

    /// 챙길 회원 — 좁은 카드를 가로로 넘긴다
    private var careMembers: some View {
        section(title: "챙길 회원") {
            CareMemberRow(members: Self.placeholderCare)
        }
    }

    /// 미작성 일지 — 면 없이 줄만 나눈다
    private var pendingLogs: some View {
        section(title: "미작성 일지", count: Self.placeholderLogs.count) {
            PendingLogList(logs: Self.placeholderLogs)
        }
    }

    /// 이번 달 — 판 하나에 숫자 셋
    private var monthSummary: some View {
        section(title: "이번 달") {
            MonthSummary(stats: Self.placeholderMonth)
        }
    }

    /// 섹션 하나 — 제목 줄 + 내용. 섹션 사이 간격을 한 곳에서 정한다.
    private func section<Content: View>(
        title: String,
        count: Int? = nil,
        actionLabel: String? = nil,
        onAction: @escaping () -> Void = {},
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: TeamFisSpacing.md) {
            SectionHeader(title: title, count: count, actionLabel: actionLabel, onAction: onAction)
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.top, TeamFisSpacing.xxxl)
    }

    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 오늘 수업을 주면 통째로 걷어낸다.
    /// 시간 순으로 이미 정렬돼 있다.
    private static let placeholderCare = [
        CareMember(name: "000", reason: "2회 남음"),
        CareMember(name: "000", reason: "3회 남음"),
        CareMember(name: "000", reason: "12일 안 옴"),
        CareMember(name: "000", reason: "21일 안 옴"),
    ]

    private static let placeholderLogs = [
        PendingLog(date: "9/6 (토)", member: "000"),
        PendingLog(date: "9/5 (금)", member: "000"),
        PendingLog(date: "9/5 (금)", member: "000"),
    ]

    private static let placeholderMonth = [
        MonthStat(value: "48", label: "세션"),
        MonthStat(value: "3", label: "신규"),
        MonthStat(value: "5", label: "재등록"),
    ]

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
