import SwiftUI

/// 수업이 끝난 뒤에 쓴 일지.
///
/// **수업 상세에서는 읽기만 한다.** 쓰는 자리는 수업 탭이다 — 한 자리에서 쓰고
/// 여러 자리에서 보는 게 맞고, 두 군데서 쓰게 하면 어느 쪽이 최신인지 알 수 없다.
struct ScheduleLog {
    let parts: [BodyPart]
    let note: String

    var isEmpty: Bool {
        parts.isEmpty && note.trimmingCharacters(in: .whitespaces).isEmpty
    }
}

/// 일지 카드 — 운동 부위 칩 + 메모.
///
/// **회원 상세의 회차 카드와 같은 판이다** (2026-09-08 대표 지시) — 같은 결의 내용이
/// 한 화면에서는 카드고 다른 화면에서는 맨바닥이면 안 읽힌다.
///
/// 회차 카드가 부위만 보여 주는 것과 달리 **메모까지 편다.** 거기는 회차를 훑는
/// 목록이고 여기는 수업 하나만 있는 화면이라 접을 이유가 없다.
///
/// **아직 안 쓴 수업에도 카드는 선다** (2026-09-08 대표 지시). 채워질 자리를 미리
/// 보여 주는 것이라 값만 비운다 — 상태마다 화면 생김새가 달라지면 안 된다.
struct ScheduleLogBlock: View {
    let log: ScheduleLog?

    var body: some View {
        CardBlock("일지") {
            if let log, !log.isEmpty {
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
            } else {
                EmptyValue()
            }
        }
    }
}

/// 세션 사인 카드 — 회원이 회차를 확인하며 남긴 서명.
///
/// 서명은 **회차를 깎았다는 증거**라 일지 바로 밑에 둔다. 일지가 무엇을 했는지라면
/// 사인은 그것을 회원이 확인했는지다.
///
/// 그림은 **더 어두운 판 위에** 얹는다. 카드와 같은 색에 그리면 종이 위 잉크가 아니라
/// 그냥 떠 있는 선으로 보인다.
struct ScheduleSignBlock: View {
    let signed: Bool

    private let signHeight: CGFloat = 96

    var body: some View {
        CardBlock("세션 사인") {
            if signed {
                SignatureShape()
                    .stroke(
                        TeamFisColor.textPrimary,
                        style: StrokeStyle(lineWidth: 2.5, lineCap: .round, lineJoin: .round)
                    )
                    .padding(TeamFisSpacing.lg)
                    .frame(maxWidth: .infinity)
                    .frame(height: signHeight)
                    .background(
                        RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                            .fill(TeamFisColor.surface2)
                    )
                    .padding(.top, TeamFisSpacing.md)
            } else {
                EmptyValue()
            }
        }
    }
}

/// 일지·사인이 같이 쓰는 판. 둘이 나란히 서므로 판이 어긋나면 바로 보인다.
private struct CardBlock<Content: View>: View {
    let title: String
    @ViewBuilder let content: Content

    init(_ title: String, @ViewBuilder content: () -> Content) {
        self.title = title
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(title)
                .font(TeamFisFont.titleSm)
                .foregroundStyle(TeamFisColor.textPrimary)
            content
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(TeamFisSpacing.lg)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1)
        )
    }
}

/// 값이 아직 없다는 표시.
///
/// 회원 상세가 빈 칸에 쓰는 것과 같은 `—` 다. "아직 안 썼어요" 같은 문구를 새로 만들지
/// 않는다 — 앱에 이미 있는 말로 족하다.
private struct EmptyValue: View {
    var body: some View {
        Text("—")
            .font(TeamFisFont.bodySm)
            .foregroundStyle(TeamFisColor.textTertiary)
            .padding(.top, TeamFisSpacing.md)
    }
}

/// 자리 표시자 서명이다. 서버가 회원이 그린 그림을 주면 통째로 걷어낸다.
///
/// 점은 0~1 로 적어 두고 칸 크기에 맞춰 늘린다 — 판 높이를 바꿔도 다시 안 그려도 된다.
/// 첫 점 하나에 이어 (제어점, 끝점) 짝이 붙는다.
private struct SignatureShape: Shape {
    /// 진폭도 간격도 **일부러 들쭉날쭉하다.** 고르게 두면 손글씨가 아니라 사인파로 보인다.
    /// 마지막 한 획만 길게 빼 흘려 쓴 끝맺음을 흉내 낸다.
    private static let stroke: [CGPoint] = [
        CGPoint(x: 0.05, y: 0.55),
        CGPoint(x: 0.09, y: 0.12), CGPoint(x: 0.15, y: 0.58),
        CGPoint(x: 0.19, y: 0.90), CGPoint(x: 0.24, y: 0.30),
        CGPoint(x: 0.30, y: 0.02), CGPoint(x: 0.33, y: 0.66),
        CGPoint(x: 0.36, y: 0.95), CGPoint(x: 0.44, y: 0.48),
        CGPoint(x: 0.50, y: 0.20), CGPoint(x: 0.55, y: 0.72),
        CGPoint(x: 0.60, y: 0.98), CGPoint(x: 0.67, y: 0.35),
        CGPoint(x: 0.73, y: 0.08), CGPoint(x: 0.78, y: 0.60),
        CGPoint(x: 0.84, y: 0.92), CGPoint(x: 0.97, y: 0.22),
    ]

    func path(in rect: CGRect) -> Path {
        func scaled(_ point: CGPoint) -> CGPoint {
            CGPoint(x: rect.minX + point.x * rect.width, y: rect.minY + point.y * rect.height)
        }

        var path = Path()
        path.move(to: scaled(Self.stroke[0]))

        var i = 1
        while i + 1 < Self.stroke.count {
            path.addQuadCurve(to: scaled(Self.stroke[i + 1]), control: scaled(Self.stroke[i]))
            i += 2
        }
        return path
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
