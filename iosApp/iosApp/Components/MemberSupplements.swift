import SwiftUI

/// 영양제 목록 — 트레이너가 권한 것이 그대로 회원 화면에도 뜬다 (HiFIS 와 같은 자리).
///
/// **줄에는 이름과 `얼마나 · 언제` 만 편다.** 다섯 칸을 다 늘어놓으면 줄 하나가
/// 카드만큼 커져서, 대여섯 개만 담아도 회원 상세가 영양제로 가득 찬다.
/// `왜?` 와 `기억하기` 는 눌러서 여는 자리에 다 있다.
struct SupplementList: View {
    let supplements: [Supplement]
    let onEdit: (Int) -> Void
    let onAdd: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            if supplements.isEmpty {
                Text("챙겨 드시면 좋을 영양제를 담아 두세요")
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textMuted)
            } else {
                ForEach(Array(supplements.enumerated()), id: \.element.id) { index, row in
                    if index > 0 { Spacer().frame(height: TeamFisSpacing.md) }
                    SupplementRow(row: row) { onEdit(index) }
                }
            }

            AddInlineButton(label: "영양제 추가", action: onAdd)
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

private struct SupplementRow: View {
    let row: Supplement
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: TeamFisSpacing.md) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(row.name)
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(TeamFisColor.textPrimary)
                    if !row.summary.isEmpty {
                        Text(row.summary)
                            .font(TeamFisFont.caption)
                            .foregroundStyle(TeamFisColor.textTertiary)
                            .lineLimit(1)
                    }
                }
                Spacer(minLength: 0)
                Image("ic_chevron_right")
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 18, height: 18)
                    .foregroundStyle(TeamFisColor.textTertiary)
            }
            .padding(TeamFisSpacing.md)
            .frame(maxWidth: .infinity)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface2)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

/// 영양제 고르기 — 자주 쓰는 표에서 고른다.
///
/// **고르면 네 칸이 함께 채워진다.** 매번 손으로 적으면 회원마다 말이 달라지고,
/// 그러면 회원이 트레이너마다 다른 안내를 받는다.
struct SupplementPickerSheet: View {
    let onPick: (Supplement) -> Void
    let onWriteMyself: () -> Void
    let onDismiss: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("영양제 고르기")
                .font(TeamFisFont.titleSm)
                .foregroundStyle(TeamFisColor.textPrimary)
            Text("고르면 얼마나 · 언제 · 왜가 함께 채워져요")
                .font(TeamFisFont.caption)
                .foregroundStyle(TeamFisColor.textTertiary)
                .padding(.top, TeamFisSpacing.xs)

            ScrollView {
                VStack(spacing: TeamFisSpacing.sm) {
                    ForEach(supplementPresets) { preset in
                        Button { onPick(preset) } label: {
                            VStack(alignment: .leading, spacing: 2) {
                                Text(preset.name)
                                    .font(TeamFisFont.bodySm)
                                    .foregroundStyle(TeamFisColor.textPrimary)
                                Text(preset.reason)
                                    .font(TeamFisFont.caption)
                                    .foregroundStyle(TeamFisColor.textTertiary)
                                    .lineLimit(2)
                                    .multilineTextAlignment(.leading)
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(TeamFisSpacing.md)
                            .background(
                                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                                    .fill(TeamFisColor.surface2)
                            )
                            .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
            .padding(.top, TeamFisSpacing.lg)

            HStack(spacing: TeamFisSpacing.md) {
                SessionButton(label: "직접 적기", background: TeamFisColor.surface2,
                              content: TeamFisColor.textSecondary, action: onWriteMyself)
                SessionButton(label: "닫기", background: TeamFisColor.surface2,
                              content: TeamFisColor.textSecondary, action: onDismiss)
            }
            .frame(height: 48)
            .padding(.top, TeamFisSpacing.lg)
        }
        .padding(TeamFisSpacing.xl)
        .presentationDetents([.large])
    }
}

/// 영양제 담기 · 고치기 — 다섯 칸.
///
/// **이름이 비면 저장이 안 찬다.** 브랜드 면에 흰 글씨를 두면 눌러도 아무 일이
/// 안 일어나는 버튼이 살아 있는 것처럼 보인다.
struct SupplementEditSheet: View {
    @State var row: Supplement
    let editing: Bool
    let onSave: (Supplement) -> Void
    let onDelete: () -> Void
    let onDismiss: () -> Void

    private var filled: Bool { !row.name.trimmingCharacters(in: .whitespaces).isEmpty }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(editing ? "영양제 고치기" : "영양제 담기")
                .font(TeamFisFont.titleSm)
                .foregroundStyle(TeamFisColor.textPrimary)
                .padding(.bottom, TeamFisSpacing.lg)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    FieldLabel("영양제")
                    FormField(text: $row.name, hint: "오메가3")

                    FieldLabel("얼마나?").padding(.top, TeamFisSpacing.md)
                    FormField(text: $row.dose, hint: "1000~3000mg")

                    FieldLabel("언제?").padding(.top, TeamFisSpacing.md)
                    FormField(text: $row.timing, hint: "아침식후")

                    FieldLabel("왜?").padding(.top, TeamFisSpacing.md)
                    FormField(text: $row.reason, hint: "성인병 예방, 염증완화", lines: 2)

                    FieldLabel("기억하기").padding(.top, TeamFisSpacing.md)
                    FormField(text: $row.note, hint: "식사 직후", lines: 2)
                }
            }

            HStack(spacing: TeamFisSpacing.md) {
                if editing {
                    SessionButton(label: "빼기", background: TeamFisColor.surface2,
                                  content: TeamFisColor.brand, action: onDelete)
                } else {
                    SessionButton(label: "취소", background: TeamFisColor.surface2,
                                  content: TeamFisColor.textSecondary, action: onDismiss)
                }
                SessionButton(
                    label: editing ? "수정" : "담기",
                    background: filled ? TeamFisColor.brand : TeamFisColor.surface2,
                    content: filled ? TeamFisColor.textPrimary : TeamFisColor.textTertiary
                ) {
                    if filled { onSave(row) }
                }
            }
            .frame(height: 48)
            .padding(.top, TeamFisSpacing.lg)
        }
        .padding(TeamFisSpacing.xl)
        .presentationDetents([.large])
    }
}
