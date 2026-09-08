import SwiftUI

/// 칸 이름 — 입력칸 바로 위에 붙는다.
struct FieldLabel: View {
    let text: String

    init(_ text: String) { self.text = text }

    var body: some View {
        Text(text)
            .font(TeamFisFont.bodySm)
            .foregroundStyle(TeamFisColor.textSecondary)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.leading, TeamFisSpacing.xs)
            .padding(.bottom, TeamFisSpacing.sm)
    }
}

/// 글자 입력칸.
///
/// 자리글씨 색을 우리가 정해야 해서 `TextField` 의 기본 placeholder 를 안 쓴다 —
/// 기본은 시스템 회색이라 우리 `textTertiary` 와 어긋난다.
struct FormField: View {
    @Binding var text: String
    let hint: String
    var keyboard: UIKeyboardType = .default
    var lines: Int = 1

    var body: some View {
        ZStack(alignment: .topLeading) {
            if text.isEmpty {
                Text(hint)
                    .font(TeamFisFont.body)
                    .foregroundStyle(TeamFisColor.textTertiary)
            }
            if lines == 1 {
                TextField("", text: $text)
                    .font(TeamFisFont.body)
                    .foregroundStyle(TeamFisColor.textPrimary)
                    .keyboardType(keyboard)
                    .tint(TeamFisColor.brand)
            } else {
                TextEditor(text: $text)
                    .font(TeamFisFont.body)
                    .foregroundStyle(TeamFisColor.textPrimary)
                    .tint(TeamFisColor.brand)
                    .scrollContentBackground(.hidden)
                    .frame(height: CGFloat(lines) * 22)
            }
        }
        .padding(.horizontal, TeamFisSpacing.lg)
        .padding(.vertical, 15)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1)
        )
    }
}

/// 눌러서 고르는 칸 — 날짜·회원처럼 **손으로 못 적는 값**.
///
/// 비었으면 칸 이름이 흐리게 서 있고, 고르면 그 자리에 값이 들어온다.
/// 값이 있으면 오른쪽이 `×` 로 바뀌어 비울 수 있다.
struct PickerField: View {
    let label: String
    let value: String?
    let onTap: () -> Void
    var onClear: (() -> Void)?

    var body: some View {
        HStack(spacing: 0) {
            Button(action: onTap) {
                HStack(spacing: 0) {
                    Text(value ?? label)
                        .font(TeamFisFont.body)
                        .foregroundStyle(value == nil ? TeamFisColor.textTertiary : TeamFisColor.textPrimary)
                    Spacer(minLength: 0)
                }
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)

            if let value, !value.isEmpty, let onClear {
                Button(action: onClear) {
                    Image("ic_close")
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: 18, height: 18)
                        .frame(width: TeamFisSize.minTouchTarget, height: TeamFisSize.minTouchTarget)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
                .foregroundStyle(TeamFisColor.textTertiary)
                .accessibilityLabel("비우기")
            } else {
                Image("ic_chevron_right")
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 18, height: 18)
                    .foregroundStyle(TeamFisColor.textTertiary)
                    .padding(.trailing, TeamFisSpacing.sm)
            }
        }
        .padding(.leading, TeamFisSpacing.lg)
        .padding(.trailing, TeamFisSpacing.sm)
        .frame(height: 52)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1)
        )
    }
}

/// 모드 고르개 — 트랙 위에서 **알약 하나가 미끄러진다.**
///
/// 칸마다 따로 켜고 끄면 옮기는 동안 둘 다 켜져 보이거나 툭 튄다.
///
/// 브랜드 레드를 안 쓴다. 필터 칩은 **골라도 되고 안 골라도 되는** 것이라 선택이
/// 튀어야 하지만, 여기는 늘 둘 중 하나가 켜져 있다 — 자리만 알려 주면 된다.
/// 빨강은 아래 등록 버튼이 가져간다.
struct SegmentedTabs: View {
    let labels: [String]
    let selected: Int
    let onSelect: (Int) -> Void

    private let trackPadding: CGFloat = 4

    var body: some View {
        GeometryReader { proxy in
            let width = (proxy.size.width - trackPadding * 2) / CGFloat(labels.count)
            ZStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface2)
                    .frame(width: width)
                    .offset(x: width * CGFloat(selected))
                    .animation(TeamFisMotion.base, value: selected)

                HStack(spacing: 0) {
                    ForEach(Array(labels.enumerated()), id: \.offset) { index, label in
                        Button(action: { onSelect(index) }) {
                            Text(label)
                                .font(TeamFisFont.bodySm)
                                .foregroundStyle(
                                    index == selected ? TeamFisColor.textPrimary : TeamFisColor.textTertiary
                                )
                                .frame(width: width)
                                .frame(maxHeight: .infinity)
                                .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
            .padding(trackPadding)
        }
        .frame(height: TeamFisSize.segment)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1)
        )
    }
}

/// 여럿 중 하나 고르는 칩 무리 — 방문 경로 같은 것.
///
/// 넘치면 다음 줄로 흘린다 (`FlowLayout`). 가로 스크롤로 만들면 뒤쪽 갈래가
/// 숨어서 고를 수 있는 것이 몇 개인지 모른다.
struct ChoiceChips: View {
    let labels: [String]
    let selected: Int?
    let onSelect: (Int) -> Void

    var body: some View {
        FlowLayout(spacing: TeamFisSpacing.sm) {
            ForEach(Array(labels.enumerated()), id: \.offset) { index, label in
                Button(action: { onSelect(index) }) {
                    Text(label)
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(
                            index == selected ? TeamFisColor.textPrimary : TeamFisColor.textSecondary
                        )
                        .padding(.horizontal, TeamFisSpacing.md)
                        .frame(height: TeamFisSize.chip)
                        .background(
                            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                                .fill(index == selected ? TeamFisColor.brand : TeamFisColor.surface1)
                        )
                        .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
            }
        }
    }
}

/// 켜고 끄는 줄 — 제목 + 설명 + 스위치.
struct ToggleRow: View {
    let title: String
    let description: String
    @Binding var isOn: Bool

    var body: some View {
        HStack(spacing: TeamFisSpacing.md) {
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textPrimary)
                Text(description)
                    .font(TeamFisFont.caption)
                    .foregroundStyle(TeamFisColor.textTertiary)
            }
            Spacer(minLength: 0)
            Toggle("", isOn: $isOn)
                .labelsHidden()
                .tint(TeamFisColor.brand)
        }
        .padding(.horizontal, TeamFisSpacing.lg)
        .padding(.vertical, TeamFisSpacing.md)
        .background(
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1)
        )
    }
}

/// 화면 아래 고정 버튼.
///
/// **필수 칸이 다 차야 빨갛게 찬다.** 덜 찼을 때 눌리지 않게 막는 대신
/// 눌리게 두고 **무엇이 비었는지 말해 준다** — 왜 안 되는지 모르는 것보다 낫다.
struct BottomActionButton: View {
    let label: String
    let filled: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(label)
                .font(TeamFisFont.titleSm)
                .foregroundStyle(filled ? TeamFisColor.textPrimary : TeamFisColor.textTertiary)
                .frame(maxWidth: .infinity)
                .frame(height: TeamFisSize.actionButton)
                .background(
                    RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                        .fill(filled ? TeamFisColor.brand : TeamFisColor.surface2)
                )
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
