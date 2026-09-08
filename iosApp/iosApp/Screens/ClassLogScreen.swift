import SwiftUI

/// 일지 작성 — 수업 탭의 일지 목록에서 한 건을 눌렀을 때.
///
/// **HiFIS 의 운동 일지 서식을 가져왔다** (2026-09-08 대표 지시) — 수업 내용 ·
/// 웨이트 표 · 유산소 표 · 피드백. 생김새만 TeamFIS 것으로 갈았다.
///
/// **가져오면서 뺀 것이 둘이다.**
/// - `수업 날짜` — 거기는 일지를 따로 쓰지만 여기는 **수업에서 들어온다.** 날짜가
///   이미 정해져 있어서 다시 받으면 두 벌이 된다 (머리에 이미 서 있다)
/// - `사진 · 영상` — 올릴 곳이 아직 없다
///
/// 머리 모양은 세션 사인 화면과 같다 — 형제 화면이라 나란해야 한다.
///
/// **잎 화면이다** — 뿌리(`AppRoot`)가 오른쪽에서 밀어 넣어 하단 유리 바까지 덮는다.
struct ClassLogScreen: View {
    let todo: ClassTodo
    let onBack: () -> Void

    @State private var title = ""
    @State private var feedback = ""

    /// 처음부터 빈 줄 하나씩 둔다 — 누르지 않아도 바로 적는다 (HiFIS 와 같은 규칙)
    @State private var weights: [WeightEntry] = [WeightEntry()]
    @State private var cardio: [CardioEntry] = [CardioEntry()]

    /// 부위 고르개를 띄운 줄. 없으면 안 뜬다
    @State private var partRow: Int?

    var body: some View {
        VStack(spacing: 0) {
            DetailHeader(title: "일지 작성", onBack: onBack)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    HStack(spacing: TeamFisSpacing.md) {
                        Text("\(todo.item.member) 회원님")
                            .font(TeamFisFont.titleLg)
                            .foregroundStyle(TeamFisColor.textPrimary)
                        Spacer(minLength: 0)
                        Text(todo.item.progress)
                            .font(TeamFisFont.bodySm.monospacedDigit())
                            .foregroundStyle(TeamFisColor.textSecondary)
                    }

                    Text("\(dayTitle(todo.item.at)) \(ampmTime(todo.item.at))")
                        .font(TeamFisFont.bodySm.monospacedDigit())
                        .foregroundStyle(TeamFisColor.textSecondary)
                        .padding(.top, TeamFisSpacing.sm)

                    FieldLabel("수업 내용")
                        .padding(.top, TeamFisSpacing.xl)
                    FormField(text: $title, hint: "예) 가슴, 삼두")

                    FieldLabel("웨이트 운동")
                        .padding(.top, TeamFisSpacing.xl)
                    TableBox {
                        ForEach(Array(weights.enumerated()), id: \.element.id) { index, _ in
                            if index > 0 { Spacer().frame(height: TeamFisSpacing.md) }
                            WeightRowFields(
                                entry: $weights[index],
                                number: index + 1,
                                onPickPart: { partRow = index },
                                onRemove: {
                                    // 마지막 한 줄은 비우기만 한다 — 표가 통째로 사라지면
                                    // 다시 어디를 눌러야 할지 알 수 없다
                                    if weights.count > 1 {
                                        weights.remove(at: index)
                                    } else {
                                        weights[0] = WeightEntry()
                                    }
                                }
                            )
                        }
                    }
                    AddRowButton(label: "운동 추가") { weights.append(WeightEntry()) }
                        .padding(.top, TeamFisSpacing.sm)

                    FieldLabel("유산소 운동")
                        .padding(.top, TeamFisSpacing.xl)
                    TableBox {
                        ForEach(Array(cardio.enumerated()), id: \.element.id) { index, _ in
                            if index > 0 { Spacer().frame(height: TeamFisSpacing.sm) }
                            CardioRowFields(
                                entry: $cardio[index],
                                onRemove: {
                                    if cardio.count > 1 {
                                        cardio.remove(at: index)
                                    } else {
                                        cardio[0] = CardioEntry()
                                    }
                                }
                            )
                        }
                    }
                    AddRowButton(label: "유산소 추가") { cardio.append(CardioEntry()) }
                        .padding(.top, TeamFisSpacing.sm)

                    FieldLabel("피드백")
                        .padding(.top, TeamFisSpacing.xl)
                    FormField(text: $feedback, hint: "오늘 수업에서 느낀 점 · 다음에 볼 것", lines: 4)
                }
                .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                .padding(.bottom, TeamFisSpacing.xxxl)
            }

            // 수업 내용만 있으면 보낼 수 있다 — 표는 비워 두고 글로만 적는 날도 있다
            BottomActionButton(
                label: "저장",
                filled: !title.trimmingCharacters(in: .whitespaces).isEmpty,
                // TODO(서버): 일지 저장 API 가 붙으면 실제로 보낸다
                action: {}
            )
            .padding(.horizontal, TeamFisSpacing.screenHorizontal)
            .padding(.vertical, TeamFisSpacing.md)
        }
        .sheet(item: Binding(get: { partRow.map(RowIndex.init) }, set: { partRow = $0?.value })) { row in
            BodyPartPicker(
                selected: weights[safe: row.value]?.part,
                onPick: { part in
                    if weights.indices.contains(row.value) { weights[row.value].part = part }
                    partRow = nil
                },
                onDismiss: { partRow = nil }
            )
        }
    }
}

/// `.sheet(item:)` 은 `Identifiable` 을 받는다 — 줄 번호를 담아 넘긴다
private struct RowIndex: Identifiable {
    let value: Int
    var id: Int { value }

    init(_ value: Int) { self.value = value }
}

private extension Array {
    subscript(safe index: Int) -> Element? {
        indices.contains(index) ? self[index] : nil
    }
}
