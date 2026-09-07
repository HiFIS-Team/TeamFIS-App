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
struct MemberFilterBar: View {
    let selected: MemberStatus?
    let counts: [MemberStatus: Int]
    let onSelect: (MemberStatus) -> Void

    var body: some View {
        HStack(spacing: TeamFisSpacing.sm) {
            ForEach(MemberStatus.allCases, id: \.self) { status in
                FilterChip(
                    label: status.label,
                    count: counts[status] ?? 0,
                    selected: selected == status
                ) {
                    onSelect(status)
                }
            }

            Spacer(minLength: 0)
        }
        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
    }
}

/// 필터 칩 하나 — 이름 + 숫자. 고르면 브랜드 색으로 찬다.
private struct FilterChip: View {
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
