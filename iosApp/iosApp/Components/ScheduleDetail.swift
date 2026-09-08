import SwiftUI

/// 수업이 끝난 뒤에 쓴 일지.
///
/// **수업 상세에서는 읽기만 한다.** 쓰는 자리는 수업 탭이다 — 한 자리에서 쓰고
/// 여러 자리에서 보는 게 맞고, 두 군데서 쓰게 하면 어느 쪽이 최신인지 알 수 없다.
struct ScheduleLog {
    let parts: [BodyPart]
    let note: String
}

/// 일지 카드 — 운동 부위 칩 + 메모.
///
/// **회원 상세의 회차 카드와 같은 판이다** (2026-09-08 대표 지시) — 같은 결의 내용이
/// 한 화면에서는 카드고 다른 화면에서는 맨바닥이면 안 읽힌다.
///
/// 회차 카드가 부위만 보여 주는 것과 달리 **메모까지 편다.** 거기는 회차를 훑는
/// 목록이고 여기는 수업 하나만 있는 화면이라 접을 이유가 없다.
struct ScheduleLogBlock: View {
    let log: ScheduleLog

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("일지")
                .font(TeamFisFont.titleSm)
                .foregroundStyle(TeamFisColor.textPrimary)

            if !log.parts.isEmpty {
                BodyPartChips(parts: log.parts)
                    .padding(.top, TeamFisSpacing.md)
            }

            if !log.note.trimmingCharacters(in: .whitespaces).isEmpty {
                Text(log.note)
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textSecondary)
                    .fixedSize(horizontal: false, vertical: true)
                    .padding(.top, TeamFisSpacing.md)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(TeamFisSpacing.lg)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1)
        )
    }
}

/// `3월 21일 (토)` — 수업 상세의 머리에 선다.
func dayTitle(_ date: Date) -> String {
    let parts = TeamFisCalendar.calendar.dateComponents([.month, .day], from: date)
    return "\(parts.month ?? 0)월 \(parts.day ?? 0)일 (\(TeamFisCalendar.weekdayLabel(date)))"
}

/// `2026. 3. 21. (토)` — 회원 상세의 날짜 표기에 요일만 더했다. 무슨 요일 수업인지가 중요해서다.
func dayLabel(_ date: Date) -> String {
    let parts = TeamFisCalendar.calendar.dateComponents([.year, .month, .day], from: date)
    return "\(parts.year ?? 0). \(parts.month ?? 0). \(parts.day ?? 0). (\(TeamFisCalendar.weekdayLabel(date)))"
}

/// 날짜만 갈아 끼운다 — 시각은 그대로 둔다.
func onDate(_ time: Date, _ date: Date) -> Date {
    let calendar = TeamFisCalendar.calendar
    let clock = calendar.dateComponents([.hour, .minute], from: time)
    return calendar.date(
        bySettingHour: clock.hour ?? 0,
        minute: clock.minute ?? 0,
        second: 0,
        of: date
    ) ?? time
}
