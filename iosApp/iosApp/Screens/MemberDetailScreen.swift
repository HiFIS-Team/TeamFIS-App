import SwiftUI

/// 회원 상세 — 목록에서 한 명을 눌렀을 때.
///
/// **워드마크 헤더가 없다.** 파고든 자리라 지금 필요한 것은 돌아갈 길이다
/// (`MemberDetailTopBar` 가 뒤로가기만 그린다).
///
/// 순서는 **사람 → 등록 → 회차**다. 누구인지 알고, 뭘 끊었는지 보고,
/// 그 아래에서 회차를 처리한다.
struct MemberDetailScreen: View {
    let member: Member
    let onBack: () -> Void

    @State private var productIndex = 0
    @State private var productExpanded = false

    private var detail: MemberDetail { Self.placeholderDetail(for: member) }

    var body: some View {
        // 홈·회원 목록과 같은 `VStack { 고정; ScrollView }` 모양을 지킨다
        VStack(spacing: 0) {
            MemberDetailTopBar(onBack: onBack)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    MemberProfile(member: detail)
                        .padding(.top, TeamFisSpacing.sm)

                    Rectangle()
                        .fill(TeamFisColor.divider)
                        .frame(height: 1)
                        .padding(.top, TeamFisSpacing.xl)
                        .padding(.horizontal, TeamFisSpacing.screenHorizontal)

                    ProductSelector(
                        products: detail.products,
                        selectedIndex: productIndex,
                        expanded: productExpanded,
                        onToggle: { productExpanded.toggle() },
                        onSelect: {
                            productIndex = $0
                            productExpanded = false
                        }
                    )
                    .padding(.top, TeamFisSpacing.sm)

                    VStack(spacing: TeamFisSpacing.md) {
                        ForEach(detail.products[productIndex].sessions) { session in
                            // 회차 처리는 서버가 붙어야 한다
                            SessionCard(session: session)
                        }
                    }
                    .padding(.top, TeamFisSpacing.sm)
                    .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                }
                // 접힌 유리 바가 마지막 카드를 덮지 않게 (값의 근거는 토큰 주석에)
                .padding(.bottom, TeamFisSize.bottomBarClearance)
            }
        }
    }

    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 회원 상세를 주면 통째로 걷어낸다.
    /// 목록에서 누른 회원의 이름만 이어 받는다.
    private static func placeholderDetail(for member: Member) -> MemberDetail {
        MemberDetail(
            name: member.name,
            gender: "남",
            phone: "010-1234-4564",
            birth: "1999. 12. 12.",
            products: [
                MemberProduct(
                    name: "얼리버드 20회",
                    sessions: [
                        MemberSession(round: 3, at: "2026.03.21 (토) 10:00", status: .scheduled),
                        MemberSession(
                            round: 2, at: "2026.03.18 (수) 19:30", status: .done,
                            parts: [.chest, .leg, .back, .arm, .shoulder, .cardio]
                        ),
                        MemberSession(
                            round: 1, at: "2026.03.14 (토) 10:00", status: .done,
                            parts: [.back, .arm]
                        ),
                    ]
                ),
                MemberProduct(
                    name: "PT 30회",
                    sessions: [
                        MemberSession(
                            round: 30, at: "2026.02.27 (금) 20:00", status: .done,
                            parts: [.leg, .cardio]
                        ),
                        MemberSession(round: 29, at: "2026.02.24 (화) 20:00", status: .noShow),
                    ]
                ),
            ]
        )
    }
}
