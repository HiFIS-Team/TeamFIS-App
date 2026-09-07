import SwiftUI

/// 회원 상세 — 목록에서 한 명을 눌렀을 때 펼쳐지는 것.
struct MemberDetail {
    let name: String
    /// `남` · `여` — 이름 옆 괄호에 들어간다
    let gender: String
    let phone: String
    let birth: String
    /// 등록 상품. 여러 개면 위 줄에서 골라 가며 본다
    let products: [MemberProduct]
}

/// 등록 상품 하나 — 회차가 이 밑에 달린다.
struct MemberProduct: Identifiable {
    let id = UUID()
    let name: String
    let sessions: [MemberSession]
}

/// 회차 한 건.
struct MemberSession: Identifiable {
    let id = UUID()
    let round: Int
    /// `2026.03.21 (토) 10:00`
    let at: String
    let status: SessionStatus
    /// 수업이 끝난 회차만 채워진다
    var parts: [BodyPart] = []
}

/// 회차 상태.
enum SessionStatus {
    case scheduled, done, noShow

    var label: String {
        switch self {
        case .scheduled: "수업예정"
        case .done: "수업완료"
        case .noShow: "노쇼"
        }
    }
}

/// 그날 한 운동 부위. 칩에 아이콘과 함께 붙는다.
enum BodyPart: CaseIterable {
    case chest, leg, back, arm, shoulder, cardio

    var label: String {
        switch self {
        case .chest: "CHEST"
        case .leg: "LEG"
        case .back: "BACK"
        case .arm: "ARM"
        case .shoulder: "SHOULDER"
        case .cardio: "CARDIO"
        }
    }

    var icon: String {
        switch self {
        case .chest: "ic_part_chest"
        case .leg: "ic_part_leg"
        case .back: "ic_part_back"
        case .arm: "ic_part_arm"
        case .shoulder: "ic_part_shoulder"
        case .cardio: "ic_part_cardio"
        }
    }
}

/// 상세 맨 위 줄 — **뒤로가기만 있다.**
///
/// 여기서는 `TeamFIS` 워드마크를 안 쓴다. 상세는 목록에서 파고든 자리라
/// 지금 필요한 것은 브랜드가 아니라 **돌아갈 길**이다.
struct MemberDetailTopBar: View {
    let onBack: () -> Void

    var body: some View {
        HStack(spacing: 0) {
            Button(action: onBack) {
                Image("ic_chevron_left")
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: Header.icon, height: Header.icon)
                    .frame(width: Header.touchTarget, height: Header.touchTarget)
                    .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .foregroundStyle(TeamFisColor.textPrimary)
            .accessibilityLabel("뒤로")

            Spacer(minLength: 0)
        }
        .frame(height: Header.height)
        .padding(.horizontal, Header.screenHorizontal - (Header.touchTarget - Header.icon) / 2)
    }
}

/// 이름 줄 + 연락처 줄.
///
/// 이름이 이 화면에서 제일 큰 글자다. 전화는 **이름 줄 오른쪽 끝**에 둔다 —
/// 번호를 눈으로 읽고 손으로 옮겨 적는 일이 없어야 한다.
struct MemberProfile: View {
    let member: MemberDetail
    var onCall: () -> Void = {}

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: TeamFisSpacing.md) {
                Text("\(member.name) (\(member.gender))")
                    .font(TeamFisFont.titleLg)
                    .foregroundStyle(TeamFisColor.textPrimary)

                Spacer(minLength: 0)

                Button(action: onCall) {
                    Image("ic_call")
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: Header.icon, height: Header.icon)
                        .frame(width: Header.touchTarget, height: Header.touchTarget)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
                .foregroundStyle(TeamFisColor.textPrimary)
                .accessibilityLabel("전화 걸기")
            }
            .padding(.leading, TeamFisSpacing.screenHorizontal)
            .padding(.trailing, Header.screenHorizontal - (Header.touchTarget - Header.icon) / 2)

            VStack(spacing: TeamFisSpacing.md) {
                InfoRow(label: "휴대폰 번호", value: member.phone)
                InfoRow(label: "생년월일", value: member.birth)
            }
            .padding(.top, TeamFisSpacing.xl)
            .padding(.horizontal, TeamFisSpacing.screenHorizontal)
        }
    }
}

/// 이름표는 왼쪽, 값은 오른쪽 끝.
private struct InfoRow: View {
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: TeamFisSpacing.md) {
            Text(label)
                .font(TeamFisFont.bodySm)
                .foregroundStyle(TeamFisColor.textTertiary)
            Spacer(minLength: 0)
            Text(value)
                // 번호·날짜라 자릿수가 바뀌어도 오른쪽 끝이 안 흔들려야 한다
                .font(TeamFisFont.bodySm.monospacedDigit())
                .foregroundStyle(TeamFisColor.textSecondary)
        }
    }
}

/// 등록 상품 고르는 줄 — `얼리버드 20회 ˅`.
///
/// 상품이 하나뿐이면 **화살표를 안 그린다.** 눌러도 아무 일 없는 것을 두면
/// 누를 수 있는 줄 알고 누르게 된다.
struct ProductSelector: View {
    let products: [MemberProduct]
    let selectedIndex: Int
    let expanded: Bool
    let onToggle: () -> Void
    let onSelect: (Int) -> Void

    private var single: Bool { products.count <= 1 }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Button(action: { if !single { onToggle() } }) {
                HStack(spacing: TeamFisSpacing.xs) {
                    Text(products[selectedIndex].name)
                        .font(TeamFisFont.titleSm)
                        .foregroundStyle(TeamFisColor.textPrimary)
                    if !single {
                        Image("ic_chevron_down")
                            .renderingMode(.template)
                            .resizable()
                            .frame(width: 18, height: 18)
                            .foregroundStyle(TeamFisColor.textSecondary)
                            .rotationEffect(.degrees(expanded ? 180 : 0))
                            .animation(TeamFisMotion.base, value: expanded)
                    }
                    Spacer(minLength: 0)
                }
                .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                .padding(.vertical, TeamFisSpacing.md)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)

            if expanded && !single {
                ForEach(Array(products.enumerated()), id: \.element.id) { index, product in
                    if index != selectedIndex {
                        Button(action: { onSelect(index) }) {
                            Text(product.name)
                                .font(TeamFisFont.bodySm)
                                .foregroundStyle(TeamFisColor.textSecondary)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                                .padding(.vertical, TeamFisSpacing.md)
                                .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
        }
    }
}

/// 회차 카드 한 장.
///
/// **끝난 회차와 안 끝난 회차의 아래가 다르다** — 예정이면 처리 버튼 둘,
/// 끝났으면 그날 한 운동 부위. 같은 자리에 다른 것이 오므로 카드 높이도 달라진다.
struct SessionCard: View {
    let session: MemberSession
    var onNoShow: () -> Void = {}
    var onDone: () -> Void = {}

    private let buttonHeight: CGFloat = 48

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: TeamFisSpacing.md) {
                Text("\(session.round)회차")
                    .font(TeamFisFont.titleSm)
                    .foregroundStyle(TeamFisColor.textPrimary)
                Spacer(minLength: 0)
                SessionBadge(status: session.status)
            }

            HStack(spacing: TeamFisSpacing.sm) {
                Text(session.at)
                    .font(TeamFisFont.bodySm.monospacedDigit())
                    .foregroundStyle(TeamFisColor.textSecondary)
                if session.status == .scheduled {
                    Image("ic_calendar")
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: 16, height: 16)
                        .foregroundStyle(TeamFisColor.textTertiary)
                }
            }
            .padding(.top, TeamFisSpacing.sm)

            if session.status == .scheduled {
                HStack(spacing: TeamFisSpacing.md) {
                    SessionButton(label: "노쇼", background: TeamFisColor.surface2,
                                  content: TeamFisColor.textSecondary, action: onNoShow)
                    SessionButton(label: "완료", background: TeamFisColor.brand,
                                  content: TeamFisColor.textPrimary, action: onDone)
                }
                .frame(height: buttonHeight)
                .padding(.top, TeamFisSpacing.lg)
            } else if !session.parts.isEmpty {
                BodyPartChips(parts: session.parts)
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

/// 회차 처리 버튼 — 둘이 **같은 폭**이다. 하나가 넓으면 그쪽이 정답처럼 보인다.
private struct SessionButton: View {
    let label: String
    let background: Color
    let content: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(label)
                .font(TeamFisFont.bodySm)
                .foregroundStyle(content)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(
                    RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                        .fill(background)
                )
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

/// 상태 배지 — 예정만 초록으로 찬다. 나머지는 지난 일이라 가라앉힌다.
private struct SessionBadge: View {
    let status: SessionStatus

    private var filled: Bool { status == .scheduled }

    var body: some View {
        Text(status.label)
            .font(TeamFisFont.caption)
            .foregroundStyle(filled ? TeamFisColor.textPrimary : TeamFisColor.textTertiary)
            .padding(.horizontal, TeamFisSpacing.sm)
            .padding(.vertical, 3)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(filled ? TeamFisColor.scheduled : TeamFisColor.surface2)
            )
    }
}

/// 그날 한 운동 부위 — 아이콘 + 대문자 이름.
///
/// 여섯 개까지 붙어 한 줄에 안 들어가므로 **넘치면 다음 줄로 흘린다.**
/// 가로 스크롤로 만들면 뒤쪽 부위가 숨어서 그날 뭘 했는지 한눈에 안 보인다.
struct BodyPartChips: View {
    let parts: [BodyPart]

    var body: some View {
        FlowLayout(spacing: TeamFisSpacing.sm) {
            ForEach(parts, id: \.self) { part in
                HStack(spacing: TeamFisSpacing.xs) {
                    Image(part.icon)
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: 14, height: 14)
                    Text(part.label)
                        .font(TeamFisFont.caption)
                }
                .foregroundStyle(TeamFisColor.textSecondary)
                .padding(.horizontal, TeamFisSpacing.sm)
                .padding(.vertical, TeamFisSpacing.xs)
                .background(
                    RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                        .fill(TeamFisColor.surface2)
                )
            }
        }
    }
}

/// 넘치면 다음 줄로 흘리는 배치.
///
/// SwiftUI 에 이런 배치가 없어서 직접 만든다 (안드로이드는 `FlowRow` 가 해 준다).
struct FlowLayout: Layout {
    var spacing: CGFloat

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let width = proposal.width ?? .infinity
        let rows = arrange(subviews: subviews, width: width)
        let height = rows.reduce(CGFloat.zero) { $0 + $1.height + spacing } - spacing
        return CGSize(width: width, height: max(height, 0))
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        var y = bounds.minY
        for row in arrange(subviews: subviews, width: bounds.width) {
            var x = bounds.minX
            for index in row.indices {
                let size = subviews[index].sizeThatFits(.unspecified)
                subviews[index].place(at: CGPoint(x: x, y: y), proposal: ProposedViewSize(size))
                x += size.width + spacing
            }
            y += row.height + spacing
        }
    }

    /// 한 줄에 들어갈 만큼씩 끊는다.
    private func arrange(subviews: Subviews, width: CGFloat) -> [(indices: [Int], height: CGFloat)] {
        var rows: [(indices: [Int], height: CGFloat)] = []
        var current: [Int] = []
        var used: CGFloat = 0
        var height: CGFloat = 0

        for index in subviews.indices {
            let size = subviews[index].sizeThatFits(.unspecified)
            let needed = current.isEmpty ? size.width : used + spacing + size.width
            if !current.isEmpty && needed > width {
                rows.append((current, height))
                current = [index]
                used = size.width
                height = size.height
            } else {
                current.append(index)
                used = needed
                height = max(height, size.height)
            }
        }
        if !current.isEmpty { rows.append((current, height)) }
        return rows
    }
}
