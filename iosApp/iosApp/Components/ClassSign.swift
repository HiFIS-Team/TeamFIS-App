import SwiftUI

/// 손으로 긋는 서명 칸.
///
/// 획을 **화면이 들고 있고** 칸은 받아 그리기만 한다. 지우기·저장이 밖에 있어서다 —
/// 칸이 제 안에 숨겨 두면 밖에서 비울 수도 보낼 수도 없다.
///
/// 한 획은 점의 나열이고, 획이 끝날 때마다 `strokes` 에 쌓인다. 그리는 중인 획만
/// `current` 로 따로 온다 — 매번 전체를 다시 만들지 않으려는 것이다.
///
/// ⚠️ 뿌리(`AppRoot`)의 가장자리 뒤로가기와 겹치는 자리다. 여기 제스처가 자식이라
/// 먼저 먹지만, **왼쪽 끝에서 시작한 획은 뒤로가기로 새어 나갈 수 있다.**
struct SignaturePad: View {
    let strokes: [[CGPoint]]
    let current: [CGPoint]
    let onStrokeMove: (CGPoint) -> Void
    let onStrokeEnd: () -> Void

    private let strokeWidth: CGFloat = 3

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                .fill(TeamFisColor.surface1)

            // 빈 판은 그릴 수 있는 자리로 안 보인다. 한 줄만 두고 첫 획에 사라진다
            if strokes.isEmpty, current.isEmpty {
                Text("손가락으로 서명해 주세요")
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
                .onChanged { onStrokeMove($0.location) }
                .onEnded { _ in onStrokeEnd() }
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
