import SwiftUI

/// 오늘 수업 한 건. 데이터가 붙기 전까지는 화면에서 만들어 넣는다.
struct TodayClass: Identifiable {
    let id = UUID()
    let member: String
    let time: String
    let detail: String
}

/// 오늘 수업 카드 — 위에 회원 이름과 더보기, 아래에 시간·회차.
///
/// 이름이 카드에서 제일 큰 글자다. 시간·회차는 그 밑에 명암을 낮춰 깐다.
struct TodayClassCard: View {
    let item: TodayClass
    var onMore: () -> Void = {}

    private let moreIcon: CGFloat = 20
    private let moreTouchTarget: CGFloat = 28

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .top, spacing: TeamFisSpacing.md) {
                Text(item.member)
                    .font(TeamFisFont.titleLg)
                    .foregroundStyle(TeamFisColor.textPrimary)

                Spacer(minLength: 0)

                Button(action: onMore) {
                    Image("ic_more")
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: moreIcon, height: moreIcon)
                        // 아이콘은 20 이지만 누를 자리는 넉넉히 잡는다
                        .frame(width: moreTouchTarget, height: moreTouchTarget)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
                .foregroundStyle(TeamFisColor.textTertiary)
                .accessibilityLabel("더보기")
            }

            // 이름 줄과 아래 정보 사이를 벌려 카드에 숨통을 준다
            Spacer().frame(height: TeamFisSpacing.xxl)

            VStack(alignment: .leading, spacing: TeamFisSpacing.xs) {
                Text(item.time)
                    // 시간은 자릿수가 바뀌어도 줄이 안 흔들려야 한다
                    .font(TeamFisFont.bodySm.monospacedDigit())
                    .foregroundStyle(TeamFisColor.textSecondary)
                Text(item.detail)
                    .font(TeamFisFont.caption)
                    .foregroundStyle(TeamFisColor.textTertiary)
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
