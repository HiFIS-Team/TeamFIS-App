import SwiftUI

/// 세션 사인 — 수업 탭의 사인 목록에서 한 건을 눌렀을 때.
///
/// **회원이 직접 긋는 화면이다.** 트레이너가 폰을 넘겨 주는 자리라, 위에는 무엇을
/// 확인하는 건지(누구·언제·무슨 운동)만 짧게 서고 **나머지 높이는 전부 서명 칸**이다.
/// 칸이 좁으면 이름이 안 써진다.
///
/// 지우기·저장은 회차 처리 버튼과 같은 모양이다 — 화면 아래에 나란히 선다.
///
/// **잎 화면이다** — 뿌리(`AppRoot`)가 오른쪽에서 밀어 넣어 하단 유리 바까지 덮는다.
struct ClassSignScreen: View {
    let todo: ClassTodo
    let onBack: () -> Void

    /// 획은 여기서 든다 — 지우기·저장이 칸 밖에 있어서다
    @State private var strokes: [[CGPoint]] = []
    @State private var current: [CGPoint] = []

    private var signed: Bool { !strokes.isEmpty }

    var body: some View {
        VStack(spacing: 0) {
            DetailHeader(title: "세션 사인", onBack: onBack)

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

                // 오늘 무엇을 했는지 — 회원이 그걸 보고 확인하는 것이라 사인 칸 위에 있어야 한다
                if !todo.parts.isEmpty {
                    BodyPartChips(parts: todo.parts)
                        .padding(.top, TeamFisSpacing.md)
                }

                // 남는 높이를 전부 준다 — 이름을 쓸 만큼은 돼야 한다
                SignaturePad(
                    strokes: strokes,
                    current: current,
                    onStrokeMove: { current.append($0) },
                    onStrokeEnd: {
                        if current.count > 1 { strokes.append(current) }
                        current = []
                    }
                )
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding(.top, TeamFisSpacing.lg)

                HStack(spacing: TeamFisSpacing.md) {
                    SessionButton(label: "지우기", background: TeamFisColor.surface2,
                                  content: TeamFisColor.textSecondary) {
                        strokes = []
                        current = []
                    }
                    // 안 그었으면 보낼 것이 없다 — 눌리게 두되 빈 채로 둔다
                    SessionButton(
                        label: "저장",
                        background: signed ? TeamFisColor.brand : TeamFisColor.surface2,
                        content: signed ? TeamFisColor.textPrimary : TeamFisColor.textTertiary,
                        // TODO(서버): 사인 저장 API 가 붙으면 그림을 보낸다
                        action: {}
                    )
                }
                .frame(height: 48)
                .padding(.vertical, TeamFisSpacing.md)
            }
            .padding(.horizontal, TeamFisSpacing.screenHorizontal)
        }
    }
}
