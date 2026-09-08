import SwiftUI

/// 회원 상세의 구역 머리 — `운동을 하는 이유` · `영양제`.
///
/// 회원 상세는 여태 구역 없이 이어 붙었는데, 성격이 다른 덩어리가 셋(등록권 · 이유 ·
/// 영양제)이 되면서 어디서 끊기는지가 필요해졌다.
struct MemberSectionHeader: View {
    let title: String

    init(_ title: String) { self.title = title }

    var body: some View {
        Text(title)
            .font(TeamFisFont.titleSm)
            .foregroundStyle(TeamFisColor.textPrimary)
            .padding(.bottom, TeamFisSpacing.md)
    }
}

/// 운동을 하는 이유 — 번호를 매겨 적는다 (HiFIS 에서 가져왔다).
///
/// **틀을 안 씌운다.** 회원이 말한 것을 그대로 옮겨 두는 자리라 목표 체중이든
/// `결혼식` 이든 한 줄씩 적히면 된다. 갈래를 만들어 고르게 하면 회원이 한 말이 사라진다.
///
/// **회차가 아니라 사람에 붙는 값이다** — 일지에 두면 회차마다 다시 적게 된다.
struct MemberGoalList: View {
    @Binding var goals: [String]

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ForEach(goals.indices, id: \.self) { index in
                if index > 0 { Spacer().frame(height: TeamFisSpacing.sm) }
                GoalRow(index: index, text: $goals[index]) {
                    // 마지막 한 줄은 비우기만 한다 — 판이 통째로 사라지면 다시
                    // 어디를 눌러야 할지 알 수 없다 (일지 표와 같은 규칙)
                    if goals.count > 1 { goals.remove(at: index) } else { goals[0] = "" }
                }
            }

            AddInlineButton(label: "이유 추가") { goals.append("") }
                .padding(.top, TeamFisSpacing.md)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(TeamFisSpacing.lg)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1)
        )
    }
}

private struct GoalRow: View {
    let index: Int
    @Binding var text: String
    let onRemove: () -> Void

    var body: some View {
        HStack(spacing: TeamFisSpacing.sm) {
            Text("\(index + 1)")
                .font(TeamFisFont.caption.monospacedDigit())
                .foregroundStyle(TeamFisColor.textTertiary)
                .frame(width: 20, height: 20)
                .background(
                    RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                        .fill(TeamFisColor.surface2)
                )

            ZStack(alignment: .leading) {
                if text.isEmpty {
                    Text("예) 결혼식까지 -5kg")
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(TeamFisColor.textMuted)
                }
                TextField("", text: $text)
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textPrimary)
                    .tint(TeamFisColor.brand)
            }
            .padding(.horizontal, TeamFisSpacing.md)
            .frame(height: 40)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface2)
            )

            Button(action: onRemove) {
                Image("ic_close")
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 14, height: 14)
                    .foregroundStyle(TeamFisColor.textTertiary)
                    .frame(width: 28, height: 28)
                    .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .accessibilityLabel("이 줄 지우기")
        }
    }
}

/// 판 **안쪽**에 붙는 추가 줄 — `이유 추가` · `영양제 추가`.
///
/// 일지 폼의 `AddRowButton` 은 표 **밖**에 서서 제 판이 필요했지만, 여기는 이미
/// 판 안이라 또 한 겹 깔면 판 속에 판이 된다. 글자만 브랜드 색으로 둔다.
struct AddInlineButton: View {
    let label: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: TeamFisSpacing.xs) {
                Image("ic_plus")
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 16, height: 16)
                Text(label)
                    .font(TeamFisFont.bodySm)
            }
            .foregroundStyle(TeamFisColor.brand)
            .padding(.vertical, TeamFisSpacing.xs)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
