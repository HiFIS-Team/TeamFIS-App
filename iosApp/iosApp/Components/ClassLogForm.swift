import SwiftUI

/// 웨이트 한 줄 — 부위 · 운동명 · 무게 · 횟수 · 세트.
///
/// **숫자와 단위를 갈라 둔다** (HiFIS 에서 가져온 규칙). `60kg 12회` 를 통째로 치면
/// 한글↔숫자 자판을 오가야 한다. 단위가 칸에 붙박이면 자판이 숫자판으로 고정된다.
struct WeightEntry: Identifiable {
    let id = UUID()
    var part: BodyPart?
    var name = ""
    var weight = ""
    var reps = ""
    var sets = ""
}

/// 유산소 한 줄 — 운동명 · 시간(분).
struct CardioEntry: Identifiable {
    let id = UUID()
    var name = ""
    var minutes = ""
}

/// 표 칸 높이 — 화면 폼(52)보다 낮다. 한 줄에 여럿이 서서 그만큼 빽빽하다.
private let cellHeight: CGFloat = 40
private let partWidth: CGFloat = 66
private let setsWidth: CGFloat = 92
private let stepWidth: CGFloat = 26
private let timeWidth: CGFloat = 84
private let removeSize: CGFloat = 28
private let indexSize: CGFloat = 20

/// 웨이트 줄 하나.
///
/// **두 줄로 눕힌다.** 부위·운동명·무게·횟수·세트를 한 줄에 넣으면 폰에서 칸이
/// 손톱만 해진다. 위는 무슨 운동인지, 아래는 얼마나 했는지다.
///
/// 왼쪽 번호는 **줄이 늘어졌을 때 어디까지 적었는지** 놓치지 않게 하는 것이다.
struct WeightRowFields: View {
    @Binding var entry: WeightEntry
    let number: Int
    let onPickPart: () -> Void
    let onRemove: () -> Void

    var body: some View {
        HStack(alignment: .top, spacing: TeamFisSpacing.sm) {
            IndexBadge(number: number)

            VStack(spacing: TeamFisSpacing.sm) {
                HStack(spacing: TeamFisSpacing.sm) {
                    PartCell(part: entry.part, action: onPickPart)
                    CellField(text: $entry.name, hint: "운동명")
                    RemoveButton(action: onRemove)
                }

                HStack(spacing: TeamFisSpacing.sm) {
                    UnitField(text: $entry.weight, unit: "kg", decimal: true)
                    UnitField(text: $entry.reps, unit: "회")
                    SetsStepper(text: $entry.sets)
                    // 윗줄 지우기 버튼만큼 비운다 — 칸 끝이 어긋나면 표로 안 보인다
                    Spacer().frame(width: removeSize)
                }
            }
        }
    }
}

/// 유산소 줄 하나 — 웨이트와 달리 한 줄에 다 들어간다.
struct CardioRowFields: View {
    @Binding var entry: CardioEntry
    let onRemove: () -> Void

    var body: some View {
        HStack(spacing: TeamFisSpacing.sm) {
            CellField(text: $entry.name, hint: "운동명")
            UnitField(text: $entry.minutes, unit: "분")
                .frame(width: timeWidth)
            RemoveButton(action: onRemove)
        }
    }
}

/// 표를 감싸는 판 — 줄이 여럿일 때 한 덩어리로 보여야 한다.
struct TableBox<Content: View>: View {
    @ViewBuilder let content: Content

    var body: some View {
        VStack(spacing: 0) { content }
            .frame(maxWidth: .infinity)
            .padding(TeamFisSpacing.sm)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface1)
            )
    }
}

/// `+ 운동 추가` — 표 밑에 붙는 줄.
///
/// **테두리가 아니라 면으로 찬다** (2026-09-08 대표 지시). 선은 `divider`(흰색 10%)라
/// 검은 바탕에서 거의 안 보였다. 이 앱에 아웃라인 버튼은 여기 하나뿐이었고 나머지는
/// 전부 면이라, 눌리는 칸들과 같은 `surface2` 로 맞춘다.
struct AddRowButton: View {
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
            .foregroundStyle(TeamFisColor.textSecondary)
            .frame(maxWidth: .infinity)
            .frame(height: TeamFisSize.minTouchTarget)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface2)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

/// 운동 부위 고르개.
///
/// **직접 입력을 안 받는다** — HiFIS 는 자유 글자라 아무 말이나 들어갔는데,
/// 우리는 `BodyPart` 여섯 갈래로 굳혀 두었고 조회 화면이 그 아이콘으로 그린다.
/// 없는 부위를 손으로 적게 두면 그 줄만 아이콘이 없다.
struct BodyPartPicker: View {
    let selected: BodyPart?
    let onPick: (BodyPart?) -> Void
    let onDismiss: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("운동 부위")
                .font(TeamFisFont.titleSm)
                .foregroundStyle(TeamFisColor.textPrimary)

            // 유산소는 아래 제 표가 따로 있다 — 여기 두면 어디에 적을지 갈린다
            FlowLayout(spacing: TeamFisSpacing.sm) {
                ForEach(BodyPart.allCases.filter { $0 != .cardio }, id: \.self) { part in
                    partChip(part)
                }
            }
            .padding(.top, TeamFisSpacing.lg)

            HStack(spacing: TeamFisSpacing.md) {
                SessionButton(label: "비우기", background: TeamFisColor.surface2,
                              content: TeamFisColor.textSecondary) { onPick(nil) }
                SessionButton(label: "닫기", background: TeamFisColor.surface2,
                              content: TeamFisColor.textSecondary, action: onDismiss)
            }
            .frame(height: 48)
            .padding(.top, TeamFisSpacing.xl)
        }
        .padding(TeamFisSpacing.xl)
        .presentationDetents([.height(280)])
    }

    private func partChip(_ part: BodyPart) -> some View {
        let isSelected = part == selected
        return Button { onPick(part) } label: {
            HStack(spacing: TeamFisSpacing.xs) {
                Image(part.icon)
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 14, height: 14)
                Text(part.label)
                    .font(TeamFisFont.bodySm)
            }
            .foregroundStyle(isSelected ? TeamFisColor.textPrimary : TeamFisColor.textSecondary)
            .padding(.horizontal, TeamFisSpacing.md)
            .frame(height: TeamFisSize.chip)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(isSelected ? TeamFisColor.brand : TeamFisColor.surface2)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

// MARK: - 칸 조각들

/// 표 안의 글자 칸 — 화면 폼의 `FormField` 보다 낮고 좁다.
private struct CellField: View {
    @Binding var text: String
    let hint: String

    var body: some View {
        ZStack(alignment: .leading) {
            if text.isEmpty {
                Text(hint)
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textMuted)
            }
            TextField("", text: $text)
                .font(TeamFisFont.bodySm)
                .foregroundStyle(TeamFisColor.textPrimary)
                .tint(TeamFisColor.brand)
        }
        .padding(.horizontal, TeamFisSpacing.md)
        .frame(maxWidth: .infinity)
        .frame(height: cellHeight)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface2)
        )
    }
}

/// 숫자만 치는 칸 — **단위가 칸에 붙박이로 적혀 있다.**
///
/// 값이 비어 있어도 단위는 옅게 남는다. 여기가 무슨 자리인지가 그것으로 드러난다.
private struct UnitField: View {
    @Binding var text: String
    let unit: String
    var decimal = false

    var body: some View {
        HStack(spacing: 0) {
            TextField("", text: $text)
                .font(TeamFisFont.bodySm)
                .foregroundStyle(TeamFisColor.textPrimary)
                .tint(TeamFisColor.brand)
                .keyboardType(decimal ? .decimalPad : .numberPad)
                .onChange(of: text) { _, new in
                    let allowed = new.filter { $0.isNumber || (decimal && $0 == ".") }
                    if allowed != new { text = String(allowed.prefix(6)) }
                }
            Text(unit)
                .font(TeamFisFont.caption)
                .foregroundStyle(text.isEmpty ? TeamFisColor.textMuted : TeamFisColor.textTertiary)
        }
        .padding(.horizontal, TeamFisSpacing.md)
        .frame(maxWidth: .infinity)
        .frame(height: cellHeight)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface2)
        )
    }
}

/// 세트 — **눌러서 올리고 내린다.**
///
/// 세트는 3·4·5 근처에서 맴돌아 자판을 띄울 값이 아니다. 다만 20세트 같은 것도
/// 있으니 가운데는 그대로 칠 수 있게 둔다.
private struct SetsStepper: View {
    @Binding var text: String

    private func step(_ delta: Int) {
        let next = min(max((Int(text) ?? 0) + delta, 0), 99)
        text = next == 0 ? "" : "\(next)"
    }

    var body: some View {
        HStack(spacing: 0) {
            stepButton("ic_minus", "세트 줄이기") { step(-1) }

            ZStack {
                if text.isEmpty {
                    Text("세트")
                        .font(TeamFisFont.caption)
                        .foregroundStyle(TeamFisColor.textMuted)
                }
                TextField("", text: $text)
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textPrimary)
                    .tint(TeamFisColor.brand)
                    .multilineTextAlignment(.center)
                    .keyboardType(.numberPad)
                    .onChange(of: text) { _, new in
                        let digits = new.filter(\.isNumber)
                        if digits != new { text = String(digits.prefix(2)) }
                    }
            }
            .frame(maxWidth: .infinity)

            stepButton("ic_plus", "세트 늘리기") { step(1) }
        }
        .frame(width: setsWidth, height: cellHeight)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface2)
        )
    }

    private func stepButton(_ icon: String, _ label: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Image(icon)
                .renderingMode(.template)
                .resizable()
                .frame(width: 14, height: 14)
                .foregroundStyle(TeamFisColor.textSecondary)
                .frame(width: stepWidth, height: cellHeight)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
    }
}

/// 부위 칸 — 누르면 고르개가 뜬다. 고르기 전에는 `부위` 라고만 적혀 있다.
private struct PartCell: View {
    let part: BodyPart?
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Group {
                if let part {
                    HStack(spacing: 2) {
                        Image(part.icon)
                            .renderingMode(.template)
                            .resizable()
                            .frame(width: 12, height: 12)
                        Text(part.label)
                            .font(TeamFisFont.caption)
                    }
                    .foregroundStyle(TeamFisColor.textSecondary)
                } else {
                    Text("부위")
                        .font(TeamFisFont.caption)
                        .foregroundStyle(TeamFisColor.textMuted)
                }
            }
            .frame(width: partWidth, height: cellHeight)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface2)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

/// 몇 번째 운동인지 — 줄이 늘어지면 어디까지 적었는지 놓친다.
private struct IndexBadge: View {
    let number: Int

    var body: some View {
        Text("\(number)")
            .font(TeamFisFont.caption.monospacedDigit())
            .foregroundStyle(TeamFisColor.textTertiary)
            .frame(width: indexSize, height: indexSize)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface2)
            )
            .padding(.top, (cellHeight - indexSize) / 2)
    }
}

private struct RemoveButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image("ic_close")
                .renderingMode(.template)
                .resizable()
                .frame(width: 14, height: 14)
                .foregroundStyle(TeamFisColor.textTertiary)
                .frame(width: removeSize, height: cellHeight)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel("이 줄 지우기")
    }
}
