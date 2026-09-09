import SwiftUI

/// 마이 화면이 보여 주는 값 한 벌 — 나에 대한 것만 든다.
struct TrainerProfile {
    let name: String
    let branch: String
    let phone: String
    /// 이번 달 신규 등록 건수
    let newMembers: Int
    /// 이번 달 재등록 건수
    let renewals: Int
    /// 이번 달 매출 (원)
    let revenue: Int
    /// 이번 달 내 몫 (원).
    ///
    /// **앱이 요율을 모른다.** 등록권마다 결제액은 들고 있지만 요율은 지점이 정하는
    /// 값이라, 이 숫자는 서버가 계산해서 준다.
    let incentive: Int
    /// 이번 달 진행한 회차
    let doneRounds: Int
    /// 이번 달 노쇼 회차
    let noShowRounds: Int
}

/// 마이의 머리 — 지점 한 줄과 **큰 인사말**.
///
/// 대표가 준 마이페이지 짜임을 따랐다 (2026-09-09). 이 화면은 목록이 짧아서
/// 위가 비면 화면이 허전한데, **인사말이 그 자리를 채운다.** 회원 상세처럼
/// 정보를 늘어놓지 않는 것은 여기가 남이 아니라 **나**라서다 — 내 번호를 내가 볼 일은 없다.
struct TrainerHeader: View {
    let profile: TrainerProfile

    var body: some View {
        VStack(alignment: .leading, spacing: TeamFisSpacing.md) {
            Text(profile.branch)
                .font(TeamFisFont.bodySm)
                .foregroundStyle(TeamFisColor.textTertiary)
            Text("안녕하세요\n\(profile.name) 트레이너님!")
                .font(TeamFisFont.titleLg)
                .foregroundStyle(TeamFisColor.textPrimary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

/// 마이의 구역 머리 — `이번 달` · `수업` · `설정`.
///
/// 회원 일지의 `MemberSectionHeader` 와 같은 값이지만 이름이 회원 것이라 여기서는
/// 안 쓴다. 둘을 하나로 합치는 것은 세 번째 화면이 같은 줄을 쓸 때 하면 된다.
struct MySectionHeader: View {
    let title: String

    init(_ title: String) { self.title = title }

    var body: some View {
        Text(title)
            .font(TeamFisFont.titleSm)
            .foregroundStyle(TeamFisColor.textPrimary)
            .padding(.bottom, TeamFisSpacing.md)
    }
}

/// 숫자 한 줄 — 이름표 왼쪽, 값 오른쪽.
///
/// 회원 상세의 `DetailInfoRow` 와 같은 짜임이지만 **강조할 줄이 하나 있다** —
/// 내 몫이다. 트레이너가 이 화면에서 찾는 것이 그 숫자라, 나머지와 같은 무게로
/// 두면 눈이 어디에 앉을지 모른다.
struct MyStatRow: View {
    let label: String
    let value: String
    var highlight = false

    var body: some View {
        HStack(spacing: TeamFisSpacing.md) {
            Text(label)
                .font(TeamFisFont.bodySm)
                .foregroundStyle(highlight ? TeamFisColor.textSecondary : TeamFisColor.textTertiary)
            Spacer(minLength: 0)
            Text(value)
                // 돈·건수라 자릿수가 바뀌어도 오른쪽 끝이 안 흔들려야 한다
                .font(highlight
                      ? TeamFisFont.titleSm.monospacedDigit()
                      : TeamFisFont.bodySm.monospacedDigit())
                .foregroundStyle(highlight ? TeamFisColor.brand : TeamFisColor.textSecondary)
        }
    }
}

/// 숫자 줄들을 담는 판.
struct MyStatCard<Content: View>: View {
    @ViewBuilder let content: Content

    var body: some View {
        VStack(spacing: TeamFisSpacing.md) { content }
            .frame(maxWidth: .infinity)
            .padding(TeamFisSpacing.lg)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface1)
            )
    }
}

/// 내 몫 위에 긋는 얇은 선 — 위는 가게 것, 아래는 내 것이다.
struct MyStatDivider: View {
    var body: some View {
        Rectangle()
            .fill(TeamFisColor.divider)
            .frame(height: 1)
    }
}

/// 메뉴 한 줄 — 아이콘 · 글자 · (값) · 화살표.
///
/// **판을 안 깐다** (2026-09-09 대표가 준 짜임). 메뉴는 줄로 서고 갈래가 바뀌는
/// 자리에만 선을 긋는다. 줄마다 카드를 깔면 셋만 있어도 화면이 무거워진다.
///
/// 로그아웃처럼 **되돌리기 어려운 것은 브랜드 색**으로 적어 눈에 걸리게 한다.
struct MyMenuRow: View {
    let icon: String
    let label: String
    /// 오른쪽 화살표 앞에 붙는 값 — 없으면 안 그린다
    var value: String?
    var danger = false
    let action: () -> Void

    private var tint: Color { danger ? TeamFisColor.brand : TeamFisColor.textPrimary }

    var body: some View {
        Button(action: action) {
            HStack(spacing: TeamFisSpacing.md) {
                Image(icon)
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 20, height: 20)
                    .foregroundStyle(tint)
                Text(label)
                    .font(TeamFisFont.body)
                    .foregroundStyle(tint)

                Spacer(minLength: 0)

                if let value {
                    Text(value)
                        .font(TeamFisFont.bodySm.monospacedDigit())
                        .foregroundStyle(TeamFisColor.textTertiary)
                }
                Image("ic_chevron_right")
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 18, height: 18)
                    .foregroundStyle(danger ? TeamFisColor.brand : TeamFisColor.textTertiary)
            }
            .padding(.vertical, TeamFisSpacing.lg)
            .frame(maxWidth: .infinity)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

/// 메뉴 갈래를 가르는 선 — 줄과 줄 사이가 아니라 **묶음과 묶음 사이**에만 긋는다.
struct MyMenuDivider: View {
    var body: some View {
        Rectangle()
            .fill(TeamFisColor.divider)
            .frame(height: 1)
            .padding(.vertical, TeamFisSpacing.sm)
    }
}

/// 화면 맨 아래 한 줄 — 버전. 눌러도 아무 일 없다.
struct MyVersionNote: View {
    let version: String

    var body: some View {
        Text(version)
            .font(TeamFisFont.caption)
            .foregroundStyle(TeamFisColor.textMuted)
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}
