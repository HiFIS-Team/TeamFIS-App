import SwiftUI

/// 회원 상세 — 목록에서 한 명을 눌렀을 때.
///
/// 머리에 **회원 이름**이 선다 (알림함과 같은 자리). 잎 화면은 자기가 어디인지 밝혀야 하고,
/// 회원이 여럿일 때 지금 누구를 보고 있는지가 스크롤을 내려도 남아 있어야 한다.
///
/// 순서는 **사람 → 등록 → 회차**다. 누구인지 알고, 뭘 끊었는지 보고,
/// 그 아래에서 회차를 처리한다.
///
/// **잎 화면이다** — 뿌리(`AppRoot`)가 오른쪽에서 밀어 넣어 하단 유리 바까지 덮는다.
/// 그래서 바 몫의 아래 여백(`bottomBarClearance`)이 필요 없다.
struct MemberDetailScreen: View {
    let member: Member
    let onBack: () -> Void

    @State private var productIndex = 0
    @State private var productExpanded = false

    private var detail: MemberDetail { Self.placeholderDetail(for: member) }

    var body: some View {
        // 홈·회원 목록과 같은 `VStack { 고정; ScrollView }` 모양을 지킨다
        VStack(spacing: 0) {
            DetailHeader(
                title: "\(detail.name) 회원님",
                onBack: onBack,
                // TODO: 회원 설정 화면이 붙으면 연결한다
                actionIcon: "ic_setting",
                actionLabel: "회원 설정"
            )

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

                    // **이 등록권에 붙는 값** — 상품을 바꾸면 같이 바뀐다.
                    // 결제액을 프로필에 적으면 등록권이 여럿일 때 어느 것인지 알 수 없다
                    let product = detail.products[productIndex]
                    VStack(spacing: TeamFisSpacing.md) {
                        DetailInfoRow(label: "등록일", value: product.registeredAt)
                        DetailInfoRow(label: "결제액", value: "\(product.payment.commaString)원")
                        DetailInfoRow(
                            label: "회당 단가",
                            value: product.unitPrice > 0 ? "\(product.unitPrice.commaString)원" : "—"
                        )
                    }
                    .padding(.top, TeamFisSpacing.sm)
                    .padding(.horizontal, TeamFisSpacing.screenHorizontal)

                    VStack(spacing: TeamFisSpacing.md) {
                        ForEach(product.sessions) { session in
                            SessionCard(session: session)
                        }
                    }
                    .padding(.top, TeamFisSpacing.xl)
                    .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                }
                .padding(.bottom, TeamFisSpacing.xxxl)
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
            visitPath: .referral,
            referrer: "000",
            products: [
                MemberProduct(
                    name: "얼리버드 20회",
                    rounds: 20,
                    payment: 1_500_000,
                    registeredAt: "2026. 3. 10.",
                    sessions: [
                        MemberSession(round: 3, at: "2026.03.21 (토) 10:00", status: .scheduled),
                        MemberSession(
                            round: 2, at: "2026.03.18 (수) 19:30", status: .done,
                            parts: [.chest, .leg, .back, .arm, .shoulder, .cardio],
                            signed: true
                        ),
                        // 일지는 썼는데 사인을 아직 못 받은 회차 — 그냥 카드로 선다
                        MemberSession(
                            round: 1, at: "2026.03.14 (토) 10:00", status: .done,
                            parts: [.back, .arm]
                        ),
                    ]
                ),
                MemberProduct(
                    name: "PT 30회",
                    rounds: 30,
                    payment: 2_100_000,
                    registeredAt: "2025. 11. 2.",
                    sessions: [
                        MemberSession(
                            round: 30, at: "2026.02.27 (금) 20:00", status: .done,
                            parts: [.leg, .cardio], signed: true
                        ),
                        MemberSession(round: 29, at: "2026.02.24 (화) 20:00", status: .noShow),
                    ]
                ),
            ]
        )
    }
}
