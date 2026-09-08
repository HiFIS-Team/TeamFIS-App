import SwiftUI

/// 일정에 서는 수업 한 건.
///
/// **시각을 글자가 아니라 값으로 들고 있다** — 상세에서 날짜·시간을 고칠 수 있어야 해서다.
/// 카드가 읽는 `time` 은 그 값에서 나온다.
struct ScheduleClass: Identifiable, Hashable {
    let id = UUID()
    let member: String
    /// 수업 시작 시각
    let at: Date
    /// 수업 길이(분) — 시작을 옮기면 끝도 따라 움직인다
    let minutes: Int
    /// 등록 상품 — `얼리버드 20회`
    let product: String
    /// `12/30회차`
    let progress: String
    let status: SessionStatus

    /// `오후 2:00 ~ 3:00`
    var time: String { timeRange(at, minutes) }
}

/// `오후 2:00 ~ 3:00`.
///
/// 끝 시각에는 오전·오후를 **넘어갈 때만** 붙인다. 한 줄 안에서 같은 말을 두 번 하면
/// 정작 다른 쪽인 시각이 안 보인다.
func timeRange(_ at: Date, _ minutes: Int) -> String {
    let calendar = Calendar.current
    let end = calendar.date(byAdding: .minute, value: minutes, to: at) ?? at
    let sameHalf = (calendar.component(.hour, from: at) < 12)
        == (calendar.component(.hour, from: end) < 12)
    return "\(ampmTime(at)) ~ \(sameHalf ? clockTime(end) : ampmTime(end))"
}

/// `오후 2:00`
func ampmTime(_ at: Date) -> String {
    let hour = Calendar.current.component(.hour, from: at)
    return "\(hour < 12 ? "오전" : "오후") \(clockTime(at))"
}

/// `2:00` — 12시간제. 0 시와 12 시는 둘 다 `12` 다
func clockTime(_ at: Date) -> String {
    let parts = Calendar.current.dateComponents([.hour, .minute], from: at)
    let hour = (parts.hour ?? 0) % 12
    return String(format: "%d:%02d", hour == 0 ? 12 : hour, parts.minute ?? 0)
}

/// 일정의 수업 카드.
///
/// **시간이 제일 크다.** 일정 화면에서 찾는 것은 "누가"보다 "몇 시에"다 —
/// 하루를 시간 순으로 훑는 자리라 시간이 눈에 먼저 걸려야 한다.
/// (회원 상세의 회차 카드는 반대로 회차가 크다. 거기선 순서가 먼저다)
///
/// 왼쪽 점은 **상태를 색으로만** 말한다. 배지 글자를 읽지 않고도 세로로 훑을 수 있다.
struct ScheduleClassCard: View {
    let item: ScheduleClass
    var onSelect: () -> Void = {}
    /// 큰 시각 위에 붙는 날짜 한 줄. **여러 날이 섞이는 목록에서만 넘긴다.**
    /// 일정은 하루치만 세우므로 안 넘기고, 그래서 일정 카드는 예전 그대로다
    var date: String?

    var body: some View {
        Button(action: onSelect) {
            VStack(alignment: .leading, spacing: 0) {
                HStack(spacing: TeamFisSpacing.sm) {
                    Circle()
                        .fill(item.status.dotColor)
                        .frame(width: 8, height: 8)
                    Text("\(item.member) 회원님")
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(TeamFisColor.textSecondary)
                    Spacer(minLength: 0)
                    SessionBadge(status: item.status)
                }

                if let date {
                    Text(date)
                        .font(TeamFisFont.caption)
                        .foregroundStyle(TeamFisColor.textTertiary)
                        .padding(.top, TeamFisSpacing.sm)
                }

                Text(item.time)
                    // 시간은 자릿수가 바뀌어도 줄이 안 흔들려야 한다
                    .font(TeamFisFont.titleLg.monospacedDigit())
                    .foregroundStyle(TeamFisColor.textPrimary)
                    // 날짜가 붙으면 둘이 한 덩어리라 사이를 좁힌다
                    .padding(.top, date == nil ? TeamFisSpacing.sm : TeamFisSpacing.xs)

                HStack(spacing: TeamFisSpacing.sm) {
                    Text(item.product)
                        .font(TeamFisFont.caption)
                        .foregroundStyle(TeamFisColor.textTertiary)
                    Spacer(minLength: 0)
                    Text(item.progress)
                        .font(TeamFisFont.caption.monospacedDigit())
                        .foregroundStyle(TeamFisColor.textTertiary)
                }
                .padding(.top, TeamFisSpacing.md)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(TeamFisSpacing.lg)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface1)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
