import SwiftUI

/// 모션. 안드로이드 `TeamFisMotion` 과 값이 같아야 한다 — 한쪽만 고치지 않는다.
///
/// 감속 위주 이징 하나만 쓴다. 시간으로 성격을 구분한다.
enum TeamFisMotion {
    /// 눌림, 토글
    static let fast = Animation.timingCurve(0.2, 0, 0, 1, duration: 0.12)

    /// 선택 이동, 페이드
    static let base = Animation.timingCurve(0.2, 0, 0, 1, duration: 0.2)

    /// 접힘·펼침, 화면 전환
    static let slow = Animation.timingCurve(0.2, 0, 0, 1, duration: 0.32)
}
