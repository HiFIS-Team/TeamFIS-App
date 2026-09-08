import SwiftUI

/// 회원.
///
/// **보유 회원 전체가 기본**이고, 필터는 그 위에서 갈래를 좁힌다.
/// 목록이 수십 줄로 길어지므로 필터 줄은 **헤더와 함께 위에 고정**한다 —
/// 같이 흘러가면 아래에서 갈래를 바꾸려고 맨 위까지 되돌아가야 한다.
///
/// ⚠️ 홈과 같은 `VStack { 고정; ScrollView }` 모양을 지킨다. `safeAreaInset` 이나
/// `Group` 으로 감싸면 하단 유리 바가 **끝까지 내렸을 때 제멋대로 펴진다** (2026-09-07).
///
/// 한 명을 누르면 상세가 열리는데, **이 화면이 상세를 그리지 않는다.** 상세는
/// 하단 유리 바까지 덮는 잎 화면이라 뿌리(`AppRoot`)만 띄울 수 있다 — 여기서는 요청만 한다.
///
/// 회원 추가는 **필터 줄 오른쪽 끝**이다 (2026-09-08 대표 지정). iOS 엔 FAB 관습이 없고
/// 오른쪽 아래엔 다음 수업 유리 줄이 이미 떠 있다. 안드로이드는 표준대로 FAB 을 쓴다.
struct MemberScreen: View {
    var onMember: (Member) -> Void = { _ in }
    var onNotification: () -> Void = {}
    var onAddMember: () -> Void = {}

    @State private var filter: MemberStatus?

    var body: some View {
        VStack(spacing: 0) {
            AppHeader(onNotification: onNotification)

            MemberFilterBar(
                selected: filter,
                counts: Member.counts,
                // 고른 것을 다시 누르면 풀려서 전체로 돌아온다
                onSelect: { filter = (filter == $0) ? nil : $0 },
                // TODO: 회원 추가 화면이 붙으면 연결한다
                onAdd: onAddMember
            )
            .padding(.top, TeamFisSpacing.sm)
            .padding(.bottom, TeamFisSpacing.md)

            ScrollView {
                if shown.isEmpty {
                    Text("회원이 없습니다")
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(TeamFisColor.textMuted)
                        .padding(.top, TeamFisSpacing.xxxl)
                } else {
                    LazyVStack(spacing: 0) {
                        ForEach(Array(shown.enumerated()), id: \.element.id) { index, member in
                            if index > 0 {
                                MemberDivider()
                            }
                            MemberRow(member: member) { onMember(member) }
                        }
                    }
                    // 접힌 유리 바가 마지막 줄을 덮지 않게 (값의 근거는 토큰 주석에)
                    .padding(.bottom, TeamFisSize.bottomBarClearance)
                }
            }
        }
    }

    private var shown: [Member] {
        guard let filter else { return Member.placeholder }
        return Member.placeholder.filter { $0.status == filter }
    }
}
