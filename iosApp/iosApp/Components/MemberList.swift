import SwiftUI

/// 회원 상태 — 회원 목록을 가르는 세 갈래.
enum MemberStatus: CaseIterable, Hashable {
    case active, holding, expired

    var label: String {
        switch self {
        case .active: "활성"
        case .holding: "홀딩"
        case .expired: "만료"
        }
    }
}

/// 회원 한 명. 데이터가 붙기 전까지는 화면에서 만들어 넣는다.
struct Member: Identifiable, Hashable {
    let id = UUID()
    let name: String
    let status: MemberStatus
    /// `12/30회차` — 진행한 회차 / 등록 회차
    let progress: String
    /// 이름 밑 한 줄 — 마지막 수업일·홀딩 시작일 같은 것
    let detail: String
}

/// 회원 필터 — 활성 · 홀딩 · 만료.
///
/// **고른 것을 다시 누르면 풀린다.** 아무것도 안 골랐을 때가 보유 회원 전체이고,
/// 그게 이 화면의 기본이다. `전체` 칸을 따로 두지 않는 이유다.
///
/// 칩마다 숫자를 달아 **고르지 않고도 갈래별 규모**가 보이게 한다.
///
/// 오른쪽 끝에 **회원 추가**가 선다 (iOS 만). 이 줄은 스크롤을 내려도 고정이라
/// 목록이 아무리 길어도 손에 닿고, 오른쪽이 원래 비어 있던 자리다.
/// 안드로이드는 같은 일을 FAB 이 한다 — **일부러 다르다.**
struct MemberFilterBar: View {
    let selected: MemberStatus?
    let counts: [MemberStatus: Int]
    let onSelect: (MemberStatus) -> Void
    var onAdd: (() -> Void)?

    var body: some View {
        HStack(spacing: TeamFisSpacing.sm) {
            ForEach(MemberStatus.allCases, id: \.self) { status in
                CountChip(
                    label: status.label,
                    count: counts[status] ?? 0,
                    selected: selected == status
                ) {
                    onSelect(status)
                }
            }

            Spacer(minLength: 0)

            if let onAdd {
                AddMemberButton(action: onAdd)
            }
        }
        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
    }
}

/// 회원 추가 — 칩과 같은 높이의 네모에 **플러스만 브랜드 레드**다.
///
/// 바탕까지 빨갛게 채우지 않는다. 고른 필터 칩이 이미 빨간 **면**이라,
/// 같은 줄에 빨간 면이 둘이면 어느 게 선택인지 흐려진다. 면과 선으로 갈라 둔다.
private struct AddMemberButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image("ic_plus")
                .renderingMode(.template)
                .resizable()
                .frame(width: 20, height: 20)
                .foregroundStyle(TeamFisColor.brand)
                .frame(width: TeamFisSize.chip, height: TeamFisSize.chip)
                .background(
                    RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                        .fill(TeamFisColor.surface1)
                )
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel("회원 추가")
    }
}

/// 필터 칩 하나 — 이름 + 숫자. 고르면 브랜드 색으로 찬다.
/// 수업 탭 필터도 같은 칩을 쓴다. 이름을 `CountChip` 으로 둔 것은 회원 전용이 아니어서다.
struct CountChip: View {
    let label: String
    let count: Int
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: TeamFisSpacing.xs) {
                Text(label)
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(selected ? TeamFisColor.textPrimary : TeamFisColor.textSecondary)
                Text("\(count)")
                    .font(TeamFisFont.bodySm.monospacedDigit())
                    // 고른 칩은 브랜드 위라 흐린 회색이 안 보인다. 흰색을 반투명하게 깐다
                    .foregroundStyle(selected ? Color.white.opacity(0.7) : TeamFisColor.textTertiary)
            }
            .padding(.horizontal, TeamFisSpacing.md)
            .frame(height: TeamFisSize.chip)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(selected ? TeamFisColor.brand : TeamFisColor.surface1)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .animation(TeamFisMotion.fast, value: selected)
    }
}

/// 방문 경로 — **어떻게 알고 왔나.** 신규 등록에만 받는다.
///
/// 재등록은 처음 올 때 이미 정해진 값이라 다시 안 묻는다.
///
/// `지인소개` 와 `개인영업` 은 **다르다** — 앞은 기존 회원이 데려온 것이라
/// `소개한 회원` 칸이 차고, 뒤는 트레이너가 직접 딴 것이라 안 찬다.
enum VisitPath: CaseIterable, Hashable {
    case walkIn, referral, sales, blog, instagram, otToPt

    var label: String {
        switch self {
        case .walkIn: "워크인"
        case .referral: "지인소개"
        case .sales: "개인영업"
        case .blog: "블로그"
        case .instagram: "인스타"
        case .otToPt: "OT → PT"
        }
    }
}

/// 회원 한 줄 — 이름·상태 배지 / 아래 한 줄, 오른쪽 끝에 회차.
///
/// **면을 안 깔고 줄만 나눈다.** 보유 회원이 수십 명이라 카드로 쌓으면 화면이 무겁고
/// 훑어 내려가기도 어렵다.
struct MemberRow: View {
    let member: Member
    var onSelect: () -> Void = {}

    var body: some View {
        Button(action: onSelect) {
            HStack(spacing: TeamFisSpacing.md) {
                VStack(alignment: .leading, spacing: TeamFisSpacing.xs) {
                    HStack(spacing: TeamFisSpacing.sm) {
                        Text("\(member.name) 회원님")
                            .font(TeamFisFont.titleSm)
                            .foregroundStyle(TeamFisColor.textPrimary)
                        StatusBadge(status: member.status)
                    }
                    Text(member.detail)
                        .font(TeamFisFont.caption)
                        .foregroundStyle(TeamFisColor.textTertiary)
                }

                Spacer(minLength: 0)

                Text(member.progress)
                    // 회차는 자릿수가 바뀌어도 오른쪽 끝이 안 흔들려야 한다
                    .font(TeamFisFont.bodySm.monospacedDigit())
                    .foregroundStyle(TeamFisColor.textSecondary)
            }
            .padding(.horizontal, TeamFisSpacing.screenHorizontal)
            .padding(.vertical, TeamFisSpacing.lg)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

/// 상태 배지 — **활성에는 안 붙는다.**
///
/// 대부분이 활성이라 전부 붙이면 목록이 배지로 뒤덮인다. 배지는 눈에 걸려야 할
/// 예외(홀딩·만료)에만 붙인다.
private struct StatusBadge: View {
    let status: MemberStatus

    var body: some View {
        if status != .active {
            Text(status.label)
                .font(TeamFisFont.caption)
                // 만료는 재등록을 붙여야 할 자리라 브랜드 색으로 눈에 걸리게 한다
                .foregroundStyle(status == .expired ? TeamFisColor.brand : TeamFisColor.textTertiary)
                .padding(.horizontal, TeamFisSpacing.sm)
                .padding(.vertical, 2)
                .background(
                    RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                        .fill(TeamFisColor.surface2)
                )
        }
    }
}

/// 줄 사이 얇은 선.
struct MemberDivider: View {
    var body: some View {
        Rectangle()
            .fill(TeamFisColor.divider)
            .frame(height: 1)
            .padding(.horizontal, TeamFisSpacing.screenHorizontal)
    }
}

/// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 회원 목록을 주면 통째로 걷어낸다.
/// 회원 화면과 등록 화면이 나눠 쓴다.
extension Member {
    static let placeholder: [Member] = [
        Member(name: "000", status: .active, progress: "12/30회차", detail: "마지막 9/5"),
        Member(name: "000", status: .active, progress: "3/20회차", detail: "마지막 9/6"),
        Member(name: "000", status: .active, progress: "8/10회차", detail: "마지막 9/4"),
        Member(name: "000", status: .active, progress: "27/30회차", detail: "마지막 9/6"),
        Member(name: "000", status: .holding, progress: "14/40회차", detail: "9/1부터 홀딩"),
        Member(name: "000", status: .active, progress: "1/50회차", detail: "마지막 9/2"),
        Member(name: "000", status: .expired, progress: "20/20회차", detail: "8/28 만료"),
        Member(name: "000", status: .active, progress: "19/30회차", detail: "마지막 9/3"),
        Member(name: "000", status: .holding, progress: "6/20회차", detail: "8/20부터 홀딩"),
        Member(name: "000", status: .expired, progress: "30/30회차", detail: "8/11 만료"),
        Member(name: "000", status: .active, progress: "5/10회차", detail: "마지막 9/6"),
        Member(name: "000", status: .expired, progress: "10/10회차", detail: "7/30 만료"),
    ]

    /// 갈래별 인원수 — 필터 칩의 숫자다
    static let counts = Dictionary(grouping: placeholder, by: \.status).mapValues(\.count)
}
