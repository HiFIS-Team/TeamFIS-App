import SwiftUI

/// 캘린더와 오늘 수업 사이의 바로가기 두 칸 — 상담 일지 · 체형 분석.
///
/// 둘이 **같은 폭**이다. 하나가 넓으면 둘 중 하나가 더 중요해 보인다.
struct ActionTiles: View {
    var onConsultLog: () -> Void = {}
    var onBodyAnalysis: () -> Void = {}

    var body: some View {
        HStack(spacing: TeamFisSpacing.md) {
            ActionTile(
                icon: "ic_tab_log",
                title: "상담 일지",
                subtitle: "작성하기",
                action: onConsultLog
            )
            ActionTile(
                icon: "ic_analysis",
                title: "체형 분석",
                subtitle: "기록하기",
                action: onBodyAnalysis
            )
        }
    }
}

private struct ActionTile: View {
    let icon: String
    let title: String
    let subtitle: String
    let action: () -> Void

    private let iconSize: CGFloat = 24

    var body: some View {
        Button(action: action) {
            HStack(spacing: TeamFisSpacing.md) {
                Image(icon)
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: iconSize, height: iconSize)
                    .foregroundStyle(TeamFisColor.textPrimary)

                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
                        .font(TeamFisFont.titleSm)
                        .foregroundStyle(TeamFisColor.textPrimary)
                    Text(subtitle)
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(TeamFisColor.textTertiary)
                }

                Spacer(minLength: 0)
            }
            .padding(TeamFisSpacing.lg)
            .frame(maxWidth: .infinity)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface1)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
