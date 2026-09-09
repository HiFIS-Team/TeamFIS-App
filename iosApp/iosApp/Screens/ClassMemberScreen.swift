import SwiftUI

/// 회원 일지 — 수업 탭의 일지 목록에서 회원 하나를 눌렀을 때.
///
/// **HiFIS 회원 상세를 그대로 옮긴 화면이다** (2026-09-08 대표 지시) —
/// 운동을 하는 이유 → 회차 목록 + `회차 추가` → 영양제.
///
/// **회원 상세와 일부러 갈라 두었다.** 거기는 누구인지·뭘 끊었는지 보는 자리고
/// 여기는 **적는 자리**다. 같은 회원을 두 화면이 그리지만 하는 일이 다르다.
///
/// 회차는 **등록권을 안 가르고 한 줄로 세운다.** HiFIS 처럼 일지가 하나의 흐름이라
/// 여기서 등록권을 고르게 하면 지난 회차를 찾으러 위로 올라가야 한다.
///
/// **잎 화면이다** — 뿌리(`AppRoot`)가 오른쪽에서 밀어 넣어 하단 유리 바까지 덮는다.
struct ClassMemberScreen: View {
    let member: Member
    let onBack: () -> Void
    var onLog: (ClassTodo) -> Void = { _ in }

    // TODO(서버): 회원 상세 API 가 붙으면 받아 오고 고칠 때마다 보낸다
    @State private var goals: [String] = [""]
    @State private var supplements: [Supplement] = []
    @State private var pickingSupplement = false
    /// 고치는 중인 영양제. 새로 담는 중이면 자리가 `-1` 이다
    @State private var editingSupplement: EditingSupplement?

    private var detail: MemberDetail { placeholderMemberDetail(for: member) }

    /// **밑에서부터 1회차 · 2회차로 쌓인다** (2026-09-09 대표 지시) — 목록은 큰 번호가
    /// 위다. 날짜로 세우면 등록권이 바뀌는 자리에서 번호가 튀어 흐름이 끊긴다.
    /// 번호가 같으면(등록권마다 1 부터 다시 세므로) 최근 것이 위로 온다
    private var rounds: [(product: MemberProduct, session: MemberSession)] {
        var rows: [(product: MemberProduct, session: MemberSession)] = []
        for product in detail.products {
            for session in product.sessions {
                rows.append((product: product, session: session))
            }
        }
        return rows.sorted { left, right in
            let a: MemberSession = left.session
            let b: MemberSession = right.session
            if a.round != b.round { return a.round > b.round }
            return a.at > b.at
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            DetailHeader(title: "\(detail.name) 회원님", onBack: onBack)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    MemberSectionHeader("운동을 하는 이유")
                        .padding(.top, TeamFisSpacing.md)
                    MemberGoalList(goals: $goals)

                    MemberSectionHeader("운동일지")
                        .padding(.top, TeamFisSpacing.xxxl)
                    VStack(spacing: TeamFisSpacing.md) {
                        ForEach(rounds, id: \.session.id) { round in
                            SessionCard(session: round.session) {
                                onLog(Self.logTarget(detail, round.product, round.session))
                            }
                        }

                        // 아직 안 잡힌 수업을 뒤늦게 남길 때 — 마지막 다음 번호로 빈
                        // 일지를 연다. 실제 번호는 서버가 매긴다
                        AddRowButton(label: "회차 추가") { onLog(Self.newRound(detail)) }
                    }

                    MemberSectionHeader("영양제")
                        .padding(.top, TeamFisSpacing.xxxl)
                    SupplementList(
                        supplements: supplements,
                        onEdit: { editingSupplement = EditingSupplement(index: $0, row: supplements[$0]) },
                        onAdd: { pickingSupplement = true }
                    )
                }
                .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                .padding(.bottom, TeamFisSpacing.xxxl)
            }
        }
        .sheet(isPresented: $pickingSupplement) {
            SupplementPickerSheet(
                onPick: {
                    supplements.append($0)
                    pickingSupplement = false
                },
                onWriteMyself: {
                    pickingSupplement = false
                    editingSupplement = EditingSupplement(index: -1, row: Supplement(name: ""))
                },
                onDismiss: { pickingSupplement = false }
            )
        }
        .sheet(item: $editingSupplement) { editing in
            SupplementEditSheet(
                row: editing.row,
                editing: editing.index >= 0,
                onSave: { saved in
                    if editing.index >= 0 {
                        supplements[editing.index] = saved
                    } else {
                        supplements.append(saved)
                    }
                    editingSupplement = nil
                },
                onDelete: {
                    if editing.index >= 0 { supplements.remove(at: editing.index) }
                    editingSupplement = nil
                },
                onDismiss: { editingSupplement = nil }
            )
        }
    }

    /// 회차 하나를 **일지 화면이 받는 값**으로 옮긴다.
    ///
    /// 일지는 일정에서 들어오든 여기서 들어오든 같은 화면이라, 여기서 모양을 맞춘다.
    /// 수업 길이는 일지가 안 쓰므로 (`날짜 · 시작 시각` 만 편다) 기본값으로 둔다.
    static func logTarget(
        _ detail: MemberDetail,
        _ product: MemberProduct,
        _ session: MemberSession
    ) -> ClassTodo {
        ClassTodo(
            item: ScheduleClass(
                member: detail.name,
                at: session.at,
                minutes: classMinutes,
                product: product.name,
                progress: "\(session.round)/\(product.rounds)회차",
                status: session.status
            ),
            parts: session.parts
        )
    }

    /// `회차 추가` — 가장 최근 등록권의 마지막 다음 번호로 빈 일지를 연다.
    static func newRound(_ detail: MemberDetail) -> ClassTodo {
        let product = detail.products[0]
        let next = (product.sessions.map(\.round).max() ?? 0) + 1
        return ClassTodo(
            item: ScheduleClass(
                member: detail.name,
                at: Date(),
                minutes: classMinutes,
                product: product.name,
                progress: "\(next)/\(product.rounds)회차",
                status: .done
            )
        )
    }

    /// 수업 한 건의 길이. 일지는 시작 시각만 쓰지만 값이 있어야 넘길 수 있다.
    private static let classMinutes = 60
}

/// `.sheet(item:)` 은 `Identifiable` 을 받는다 — 고치는 자리와 값을 함께 넘긴다
private struct EditingSupplement: Identifiable {
    /// 새로 담는 중이면 `-1`
    let index: Int
    let row: Supplement
    var id: String { "\(index)-\(row.id)" }
}
