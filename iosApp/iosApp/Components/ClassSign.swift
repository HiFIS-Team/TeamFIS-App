import SwiftUI

/// 서명 앞에 받는 동의 줄.
///
/// **서명은 개인정보다.** 회원 폰이 아니라 트레이너 폰에 남기므로 더더욱 받아 두고
/// 시작해야 한다 (2026-09-09 대표 지시).
///
/// 회원이 읽고 누르는 줄이라 **말이 회원 것**이다 — `동의합니다`.
struct ConsentRow: View {
    @Binding var checked: Bool

    var body: some View {
        Button { checked.toggle() } label: {
            HStack(spacing: TeamFisSpacing.md) {
                ZStack {
                    RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                        .fill(checked ? TeamFisColor.brand : TeamFisColor.surface2)
                    if checked {
                        Image("ic_check")
                            .renderingMode(.template)
                            .resizable()
                            .frame(width: 14, height: 14)
                            .foregroundStyle(TeamFisColor.textPrimary)
                    }
                }
                .frame(width: 22, height: 22)

                Text("오늘 수업을 받았음을 확인하고, 서명 수집에 동의합니다")
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textSecondary)
                    .multilineTextAlignment(.leading)
                Spacer(minLength: 0)
            }
            .padding(.vertical, TeamFisSpacing.sm)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .animation(TeamFisMotion.fast, value: checked)
    }
}

/// 손으로 긋는 서명 칸.
///
/// 획을 **화면이 들고 있고** 칸은 받아 그리기만 한다. 지우기·저장이 밖에 있어서다 —
/// 칸이 제 안에 숨겨 두면 밖에서 비울 수도 보낼 수도 없다.
///
/// 한 획은 점의 나열이고, 획이 끝날 때마다 `strokes` 에 쌓인다. 그리는 중인 획만
/// `current` 로 따로 온다 — 매번 전체를 다시 만들지 않으려는 것이다.
///
/// **동의 전에는 안 열린다** (`enabled`, 2026-09-09 대표 지시). 손이 안 먹고 판이
/// 가라앉는다 — 잠긴 것이 보여야 위의 동의 줄을 찾는다.
///
/// ⚠️ 뿌리(`AppRoot`)의 가장자리 뒤로가기와 겹치는 자리다. 여기 제스처가 자식이라
/// 먼저 먹지만, **왼쪽 끝에서 시작한 획은 뒤로가기로 새어 나갈 수 있다.**
struct SignaturePad: View {
    let strokes: [[CGPoint]]
    let current: [CGPoint]
    let onStrokeMove: (CGPoint) -> Void
    let onStrokeEnd: () -> Void
    var enabled = true

    private let strokeWidth: CGFloat = 3
    /// 잠긴 판의 흐림 — 있는 것은 보이되 만질 것이 아니라는 만큼
    private let lockedAlpha: CGFloat = 0.4

    var body: some View {
        ZStack {
            // 판 전체에 `.opacity` 를 씌우지 않고 **색의 투명도**로 낮춘다 —
            // 안드로이드에서 레이어를 세웠다가 켰을 때 판이 되레 사라졌다
            // (2026-09-09 대표 확인). 두 플랫폼이 같은 방법을 쓴다
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1.opacity(enabled ? 1 : lockedAlpha))

            // 빈 판은 그릴 수 있는 자리로 안 보인다. 한 줄만 두고 첫 획에 사라진다.
            // **잠겼으면 무엇을 해야 하는지로 바뀐다** (2026-09-09 대표 지시) —
            // 어두운 판만 있으면 왜 안 그려지는지 알 수 없다.
            // 판은 가라앉아도 이 줄은 안 흐리게 둔다. 읽으라고 둔 글자다
            if strokes.isEmpty, current.isEmpty {
                Text(enabled ? "손가락으로 서명해 주세요" : "위 동의란에 체크해 주세요")
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textMuted)
            }

            Canvas { context, _ in
                for stroke in strokes + [current] {
                    guard let path = strokePath(stroke) else { continue }
                    context.stroke(
                        path,
                        with: .color(TeamFisColor.textPrimary),
                        style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round, lineJoin: .round)
                    )
                }
            }
        }
        .contentShape(Rectangle())
        .gesture(
            // 점 하나짜리 톡도 받아야 하므로 `minimumDistance` 는 0 이다
            DragGesture(minimumDistance: 0)
                .onChanged { if enabled { onStrokeMove($0.location) } }
                .onEnded { _ in if enabled { onStrokeEnd() } }
        )
    }

    /// 점을 이어 한 획을 만든다.
    ///
    /// 점끼리 직선으로 이으면 손이 꺾인 자리마다 각이 진다. **점을 제어점으로 삼고
    /// 이웃한 두 점의 가운데를 지나게** 해서 곡선으로 편다.
    private func strokePath(_ points: [CGPoint]) -> Path? {
        guard points.count > 1 else { return nil }

        var path = Path()
        path.move(to: points[0])
        for i in 1..<points.count {
            let previous = points[i - 1]
            let point = points[i]
            let mid = CGPoint(x: (previous.x + point.x) / 2, y: (previous.y + point.y) / 2)
            path.addQuadCurve(to: mid, control: previous)
        }
        path.addLine(to: points[points.count - 1])
        return path
    }
}
