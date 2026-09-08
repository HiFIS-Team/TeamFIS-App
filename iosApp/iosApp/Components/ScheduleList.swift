import SwiftUI

/// 일정에 서는 수업 한 건.
struct ScheduleClass: Identifiable {
    let id = UUID()
    let member: String
    /// `오후 2:00 ~ 3:00`
    let time: String
    /// 등록 상품 — `얼리버드 20회`
    let product: String
    /// `12/30회차`
    let progress: String
    let status: SessionStatus
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

                Text(item.time)
                    // 시간은 자릿수가 바뀌어도 줄이 안 흔들려야 한다
                    .font(TeamFisFont.titleLg.monospacedDigit())
                    .foregroundStyle(TeamFisColor.textPrimary)
                    .padding(.top, TeamFisSpacing.sm)

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
