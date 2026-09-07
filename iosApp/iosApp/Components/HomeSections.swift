import SwiftUI

/// 섹션 제목 한 줄 — 왼쪽 제목(+개수), 오른쪽 액션.
///
/// 홈의 섹션들이 **같은 줄 모양**을 쓴다. 아래 내용은 섹션마다 다르게 생겼어도
/// 제목 줄까지 다르면 화면이 흩어져 보인다.
struct SectionHeader: View {
    let title: String
    /// 제목 옆 작은 숫자 (없으면 안 그린다)
    var count: Int?
    var actionLabel: String?
    var onAction: () -> Void = {}

    var body: some View {
        HStack(alignment: .firstTextBaseline, spacing: TeamFisSpacing.sm) {
            Text(title)
                .font(TeamFisFont.titleMd)
                .foregroundStyle(TeamFisColor.textPrimary)

            if let count {
                Text("\(count)")
                    .font(TeamFisFont.titleMd)
                    .foregroundStyle(TeamFisColor.textTertiary)
            }

            Spacer(minLength: 0)

            if let actionLabel {
                Button(action: onAction) {
                    Text(actionLabel)
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(TeamFisColor.textSecondary)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
    }
}

/// 챙길 회원 한 명 — 재등록이 임박했거나 오래 안 온 회원.
struct CareMember: Identifiable {
    let id = UUID()
    let name: String
    let reason: String
}

/// 챙길 회원 — **가로로 넘긴다.**
///
/// 오늘 수업이 세로로 쌓이는 큰 카드라, 그 밑까지 같은 모양이면 화면이 지루하다.
/// 여기는 좁은 카드를 옆으로 흘려 **성격이 다른 목록**임을 모양으로 알린다.
struct CareMemberRow: View {
    let members: [CareMember]

    private let cardWidth: CGFloat = 160

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: TeamFisSpacing.md) {
                ForEach(members) { member in
                    VStack(alignment: .leading, spacing: TeamFisSpacing.xs) {
                        Text("\(member.name) 회원님")
                            .font(TeamFisFont.titleSm)
                            .foregroundStyle(TeamFisColor.textPrimary)
                        Text(member.reason)
                            // 챙길 이유가 이 카드의 요점이라 여기에만 브랜드 색을 쓴다
                            .font(TeamFisFont.bodySm)
                            .foregroundStyle(TeamFisColor.brand)
                    }
                    .frame(width: cardWidth, alignment: .leading)
                    .padding(TeamFisSpacing.lg)
                    .background(
                        RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                            .fill(TeamFisColor.surface1)
                    )
                }
            }
            .padding(.horizontal, TeamFisSpacing.screenHorizontal)
        }
    }
}

/// 아직 안 쓴 일지 한 건.
struct PendingLog: Identifiable {
    let id = UUID()
    let date: String
    let member: String
}

/// 미작성 일지 — **면을 안 깔고 줄만 나눈다.**
///
/// 카드로 만들면 오늘 수업과 구분이 안 되고, 밀린 일이 여러 건일 때 화면이 무거워진다.
/// 줄 사이 얇은 선만 두어 **처리해야 할 목록**처럼 보이게 한다.
struct PendingLogList: View {
    let logs: [PendingLog]
    var onWrite: (PendingLog) -> Void = { _ in }

    var body: some View {
        VStack(spacing: 0) {
            ForEach(Array(logs.enumerated()), id: \.element.id) { index, log in
                if index > 0 {
                    Rectangle()
                        .fill(TeamFisColor.divider)
                        .frame(height: 1)
                        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                }

                Button {
                    onWrite(log)
                } label: {
                    HStack(spacing: TeamFisSpacing.md) {
                        Text(log.date)
                            .font(TeamFisFont.bodySm.monospacedDigit())
                            .foregroundStyle(TeamFisColor.textTertiary)
                        Text("\(log.member) 회원님")
                            .font(TeamFisFont.bodySm)
                            .foregroundStyle(TeamFisColor.textPrimary)

                        Spacer(minLength: 0)

                        Text("작성")
                            .font(TeamFisFont.bodySm)
                            .foregroundStyle(TeamFisColor.brand)
                    }
                    .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                    .padding(.vertical, TeamFisSpacing.lg)
                    .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
            }
        }
    }
}

/// 이번 달 숫자 한 칸.
struct MonthStat: Identifiable {
    let id = UUID()
    let value: String
    let label: String
}

/// 이번 달 요약 — **판 하나에 숫자 셋.**
///
/// 목록이 아니라 숫자라서 목록처럼 생기면 안 된다. 한 판 안에서 세로 선으로만 나눈다.
struct MonthSummary: View {
    let stats: [MonthStat]

    var body: some View {
        HStack(spacing: 0) {
            ForEach(Array(stats.enumerated()), id: \.element.id) { index, stat in
                if index > 0 {
                    Rectangle()
                        .fill(TeamFisColor.divider)
                        .frame(width: 1)
                }
                VStack(spacing: 2) {
                    Text(stat.value)
                        // 숫자가 이 판의 주인공이라 제일 크다
                        .font(TeamFisFont.titleLg.monospacedDigit())
                        .foregroundStyle(TeamFisColor.textPrimary)
                    Text(stat.label)
                        .font(TeamFisFont.caption)
                        .foregroundStyle(TeamFisColor.textTertiary)
                }
                .frame(maxWidth: .infinity)
            }
        }
        .fixedSize(horizontal: false, vertical: true)
        .padding(.vertical, TeamFisSpacing.lg)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1)
        )
        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
    }
}
