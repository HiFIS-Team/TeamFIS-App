import SwiftUI

/// 수업 탭이 다루는 두 가지 일.
///
/// 수업이 끝나면 트레이너가 할 일은 **일지를 쓰는 것**과 **회원에게 사인을 받는 것**
/// 둘뿐이다. 탭을 그 둘로 가른다.
enum ClassFilter: CaseIterable, Hashable {
    case log, sign

    var label: String {
        switch self {
        case .log: "일지"
        case .sign: "세션 사인"
        }
    }
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
