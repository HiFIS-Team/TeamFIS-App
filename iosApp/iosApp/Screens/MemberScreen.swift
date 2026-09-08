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
struct MemberScreen: View {
    var onMember: (Member) -> Void = { _ in }

    @State private var filter: MemberStatus?

    var body: some View {
        VStack(spacing: 0) {
            AppHeader()

            MemberFilterBar(
                selected: filter,
                counts: Self.counts,
                // 고른 것을 다시 누르면 풀려서 전체로 돌아온다
                onSelect: { filter = (filter == $0) ? nil : $0 }
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
        guard let filter else { return Self.placeholderMembers }
        return Self.placeholderMembers.filter { $0.status == filter }
    }

    private static let counts = Dictionary(
        grouping: placeholderMembers, by: \.status
    ).mapValues(\.count)

    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 회원 목록을 주면 통째로 걷어낸다.
    /// 이름은 아직 다 `000` 이다.
    private static let placeholderMembers = [
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
}
