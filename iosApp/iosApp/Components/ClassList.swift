import SwiftUI

/// 수업 탭이 다루는 두 가지 일.
///
/// 수업이 끝나면 트레이너가 할 일은 **일지를 쓰는 것**과 **회원에게 사인을 받는 것**
/// 둘뿐이다. 탭을 그 둘로 가른다.
///
/// **순서가 있다.** 사인은 일지를 쓴 뒤에 받는다 (2026-09-08 대표 지시) —
/// 무엇을 했는지 적어야 회원이 그것을 확인해 줄 수 있다.
enum ClassFilter: CaseIterable, Hashable {
    case log, sign

    var label: String {
        switch self {
        case .log: "일지"
        case .sign: "세션 사인"
        }
    }
}

/// 수업 탭 목록의 한 줄 — 아직 안 닫힌 수업 하나.
///
/// 세션 사인 줄은 **일지가 이미 있는 수업**이라 `parts` 가 차 있고, 일지 줄은 비어 있다.
/// 카드가 두 갈래에서 달라 보이는 것이 그래서다 — 꾸민 게 아니라 든 것이 다르다.
struct ClassTodo: Identifiable, Hashable {
    let id = UUID()
    let item: ScheduleClass
    var parts: [BodyPart] = []
}

/// 수업 필터 — 일지 · 세션 사인.
///
/// **회원 필터와 달리 하나는 늘 골라져 있다.** 거기서는 안 고른 상태가 곧 전체지만,
/// 여기서는 성격이 다른 두 목록이라 합쳐 놓으면 그 줄이 왜 떠 있는지 알 수 없다.
///
/// 칩에 달린 숫자가 **남은 개수**다 — 고르지 않고도 어느 쪽이 밀렸는지 보인다.
struct ClassFilterBar: View {
    let selected: ClassFilter
    let counts: [ClassFilter: Int]
    let onSelect: (ClassFilter) -> Void

    var body: some View {
        HStack(spacing: TeamFisSpacing.sm) {
            ForEach(ClassFilter.allCases, id: \.self) { filter in
                CountChip(
                    label: filter.label,
                    count: counts[filter] ?? 0,
                    selected: selected == filter,
                    action: { onSelect(filter) }
                )
            }
            Spacer(minLength: 0)
        }
        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
    }
}

/// 수업 카드 — **일정 카드와 일부러 다르게 짰다** (2026-09-08 대표 지시).
///
/// 일정은 하루를 시간 순으로 훑는 자리라 **시간이 제일 크다.** 여기는 밀린 일을 처리하는
/// 자리라 **누구의 몇 회차인지**가 먼저다. 그래서 이름이 크고 시각은 한 줄로 눕는다.
///
/// **상태 점과 배지를 안 단다.** 여기 서는 것은 전부 끝난 수업이라 `수업완료` 배지가
/// 아무 말도 못 한다. 같은 값을 모두가 들고 있으면 그것은 정보가 아니다.
///
/// 날짜가 붙는 것은 여기가 **여러 날이 섞이는 목록**이어서다. 일정은 하루치만 세운다.
struct ClassCard: View {
    let todo: ClassTodo
    var onSelect: () -> Void = {}

    var body: some View {
        Button(action: onSelect) {
            VStack(alignment: .leading, spacing: 0) {
                HStack(spacing: TeamFisSpacing.md) {
                    Text("\(todo.item.member) 회원님")
                        .font(TeamFisFont.titleSm)
                        .foregroundStyle(TeamFisColor.textPrimary)
                    Spacer(minLength: 0)
                    Text(todo.item.progress)
                        // 회차는 자릿수가 바뀌어도 오른쪽 끝이 안 흔들려야 한다
                        .font(TeamFisFont.bodySm.monospacedDigit())
                        .foregroundStyle(TeamFisColor.textSecondary)
                }

                Text("\(dayTitle(todo.item.at)) \(ampmTime(todo.item.at))")
                    .font(TeamFisFont.bodySm.monospacedDigit())
                    .foregroundStyle(TeamFisColor.textSecondary)
                    .padding(.top, TeamFisSpacing.sm)

                Text(todo.item.product)
                    .font(TeamFisFont.caption)
                    .foregroundStyle(TeamFisColor.textTertiary)
                    .padding(.top, TeamFisSpacing.xs)

                // 사인만 남은 줄에는 일지가 이미 있다 — 무엇을 확인받는 건지 여기서 보인다
                if !todo.parts.isEmpty {
                    BodyPartChips(parts: todo.parts)
                        .padding(.top, TeamFisSpacing.md)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(TeamFisSpacing.lg)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(TeamFisColor.surface1)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
