import SwiftUI

/// 홈 캘린더 — 헤더 바로 밑.
///
/// 평소에는 **이번 주 한 줄**, `펼쳐보기` 를 누르면 **그 달 전체**로 늘어난다.
/// 선택은 하단 탭과 다른 규칙이다 — **브랜드 레드를 쓰지 않는다.**
/// 상시 떠 있는 것에 액센트 예산을 쓰지 않는다.
///
/// 접힌 줄의 알약은 칸을 따라 **흐른다** (`matchedGeometryEffect`).
struct HomeCalendar: View {
    @Binding var selected: Date
    /// 펼쳤을 때 보이는 달 (그 달의 아무 날)
    @Binding var month: Date
    let expanded: Bool

    /// 칸이 터치 타겟(44)보다 커야 하므로 알약 높이가 곧 행 높이다
    private let pillHeight: CGFloat = 68
    private let pillWidth: CGFloat = 44
    private let markSize: CGFloat = 26
    private let daySize: CGFloat = 40

    @Namespace private var pill

    var body: some View {
        VStack(spacing: 0) {
            if expanded {
                monthHeader
                weekdayHeader
                ForEach(Array(TeamFisCalendar.monthWeeks(of: month).enumerated()), id: \.offset) { _, week in
                    monthRow(week)
                }
            } else {
                weekStrip
            }
        }
        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
        // 줄 수가 바뀌는 것을 높이 애니메이션으로 잇는다 — 펼침이 툭 끊기면 안 된다
        .animation(TeamFisMotion.slow, value: expanded)
    }

    // MARK: - 접힌 줄

    private var weekStrip: some View {
        HStack(spacing: 0) {
            ForEach(TeamFisCalendar.week(of: selected), id: \.self) { day in
                cell(day)
            }
        }
    }

    private func cell(_ day: Date) -> some View {
        let isSelected = TeamFisCalendar.isSameDay(day, selected)

        return VStack(spacing: TeamFisSpacing.xs) {
            Text(TeamFisCalendar.weekdayLabel(day))
                .font(TeamFisFont.caption)
                .foregroundStyle(
                    isSelected
                        ? TeamFisColor.background
                        : TeamFisCalendar.weekendColor(day) ?? TeamFisColor.textTertiary
                )
                .frame(width: markSize, height: markSize)
                .background {
                    if isSelected {
                        Circle().fill(TeamFisColor.textPrimary)
                    }
                }
            Text(TeamFisCalendar.dayNumber(day))
                // 날짜는 자릿수가 바뀌어도 칸 안에서 흔들리면 안 된다
                .font(TeamFisFont.titleSm.monospacedDigit())
                .foregroundStyle(
                    isSelected
                        ? TeamFisColor.textPrimary
                        : TeamFisCalendar.weekendColor(day) ?? TeamFisColor.textSecondary
                )
                .frame(width: daySize, height: daySize)
        }
        .frame(maxWidth: .infinity)
        .frame(height: pillHeight)
        .background {
            if isSelected {
                RoundedRectangle(cornerRadius: pillHeight / 2, style: .continuous)
                    .fill(TeamFisColor.surface2)
                    .frame(width: pillWidth)
                    .matchedGeometryEffect(id: "weekPill", in: pill)
            }
        }
        .contentShape(Rectangle())
        .onTapGesture {
            guard !TeamFisCalendar.isSameDay(day, selected) else { return }
            withAnimation(TeamFisMotion.base) { selected = day }
        }
    }

    // MARK: - 펼친 달

    /// 펼친 상태의 달 머리글 — `2026년 9월` 과 앞뒤 달로 가는 화살표
    private var monthHeader: some View {
        HStack(spacing: 0) {
            Text(TeamFisCalendar.monthTitle(month))
                .font(TeamFisFont.titleMd.monospacedDigit())
                .foregroundStyle(TeamFisColor.textPrimary)
            Spacer(minLength: 0)
            // 화살표는 아래쪽 화살표 한 벌을 돌려 쓴다
            monthArrow(degrees: 90, label: "이전 달", step: -1)
            monthArrow(degrees: -90, label: "다음 달", step: 1)
        }
        .padding(.trailing, -TeamFisSpacing.sm)
        .padding(.bottom, TeamFisSpacing.md)
    }

    private func monthArrow(degrees: Double, label: String, step: Int) -> some View {
        Button {
            guard let next = TeamFisCalendar.calendar.date(byAdding: .month, value: step, to: month)
            else { return }
            month = next
        } label: {
            Image("ic_chevron_down")
                .renderingMode(.template)
                .resizable()
                .frame(width: 20, height: 20)
                .rotationEffect(.degrees(degrees))
                .foregroundStyle(TeamFisColor.textSecondary)
                .frame(width: 40, height: 40)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
    }

    /// 칸마다 요일을 반복하면 달력이 시끄럽다 — 머리글 한 줄로 뺀다
    private var weekdayHeader: some View {
        HStack(spacing: 0) {
            ForEach(TeamFisCalendar.weekdayLabels, id: \.self) { label in
                Text(label)
                    .font(TeamFisFont.caption)
                    .foregroundStyle(TeamFisCalendar.weekendColor(label: label) ?? TeamFisColor.textTertiary)
                    .frame(maxWidth: .infinity)
            }
        }
        .padding(.bottom, TeamFisSpacing.xs)
    }

    private func monthRow(_ week: [Date?]) -> some View {
        HStack(spacing: 0) {
            ForEach(Array(week.enumerated()), id: \.offset) { _, day in
                Group {
                    if let day {
                        monthDay(day)
                    } else {
                        Color.clear
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 50)
            }
        }
    }

    private func monthDay(_ day: Date) -> some View {
        let isSelected = TeamFisCalendar.isSameDay(day, selected)

        return VStack(spacing: 0) {
            Text(TeamFisCalendar.dayNumber(day))
                .font(TeamFisFont.bodySm.monospacedDigit())
                .foregroundStyle(
                    isSelected
                        ? TeamFisColor.textPrimary
                        : TeamFisCalendar.weekendColor(day) ?? TeamFisColor.textSecondary
                )
                .frame(width: daySize, height: daySize)

            // 고른 날은 **동그라미 대신 밑에 점**이다 — 달력이 조용해진다
            Circle()
                .fill(isSelected ? TeamFisColor.textPrimary : .clear)
                .frame(width: 5, height: 5)
        }
        .contentShape(Rectangle())
        .onTapGesture {
            guard !isSelected else { return }
            withAnimation(TeamFisMotion.base) { selected = day }
        }
    }
}

/// 홈 캘린더가 쓰는 날짜 계산. **주는 일요일에 시작한다** (한국 달력 관행).
///
/// 기기 지역 설정을 따르지 않고 우리가 고정한다 — 기준이 기기마다 다르면 안 된다.
enum TeamFisCalendar {
    static let calendar: Calendar = {
        var c = Calendar(identifier: .gregorian)
        c.firstWeekday = 1 // 일요일 — 한국 달력 관행
        c.locale = Locale(identifier: "ko_KR")
        return c
    }()

    /// 기기 로케일을 따르지 않고 우리가 정한다 — 한국어 전용 앱이고,
    /// 요일 한 글자는 폭이 일정해야 칸이 흔들리지 않는다.
    static let weekdayLabels = ["일", "월", "화", "수", "목", "금", "토"]

    /// [date] 가 속한 주를 일요일부터 7일 반환한다.
    static func week(of date: Date) -> [Date] {
        let start = calendar.dateInterval(of: .weekOfYear, for: date)?.start ?? date
        return (0..<7).compactMap { calendar.date(byAdding: .day, value: $0, to: start) }
    }

    /// [date] 가 속한 **달**을 주 단위로 자른다. 앞뒤 빈 칸은 `nil` 이다.
    ///
    /// 옆 달 날짜를 흐리게 채우지 않는다 — 이 달 안에서만 고르게 한다.
    static func monthWeeks(of date: Date) -> [[Date?]] {
        guard let interval = calendar.dateInterval(of: .month, for: date),
              let count = calendar.range(of: .day, in: .month, for: date)?.count
        else { return [] }

        let first = interval.start
        let lead = (calendar.component(.weekday, from: first) - calendar.firstWeekday + 7) % 7
        var days: [Date?] = Array(repeating: nil, count: lead)
        days += (0..<count).compactMap { calendar.date(byAdding: .day, value: $0, to: first) }
        if days.count % 7 != 0 {
            days += Array(repeating: nil, count: 7 - days.count % 7)
        }
        return stride(from: 0, to: days.count, by: 7).map { Array(days[$0..<($0 + 7)]) }
    }

    static func isSameDay(_ a: Date, _ b: Date) -> Bool {
        calendar.isDate(a, inSameDayAs: b)
    }

    static func dayNumber(_ date: Date) -> String {
        String(calendar.component(.day, from: date))
    }

    /// `2026년 9월`
    static func monthTitle(_ date: Date) -> String {
        let parts = calendar.dateComponents([.year, .month], from: date)
        return "\(parts.year ?? 0)년 \(parts.month ?? 0)월"
    }

    static func weekdayLabel(_ date: Date) -> String {
        weekdayLabels[calendar.component(.weekday, from: date) - 1]
    }

    /// 토요일 파랑 · 일요일 빨강 — 한국 달력 관행.
    static func weekendColor(_ date: Date) -> Color? {
        switch calendar.component(.weekday, from: date) {
        case 1: TeamFisColor.weekendSunday
        case 7: TeamFisColor.weekendSaturday
        default: nil
        }
    }

    static func weekendColor(label: String) -> Color? {
        switch label {
        case "일": TeamFisColor.weekendSunday
        case "토": TeamFisColor.weekendSaturday
        default: nil
        }
    }
}
