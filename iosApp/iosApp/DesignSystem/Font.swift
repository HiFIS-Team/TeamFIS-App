import SwiftUI

/// 타입 스케일. 서체는 아직 시스템 기본이다 (전용 서체가 정해지면 여기만 고친다).
/// 크기·굵기는 안드로이드 `TeamFisType` 과 같아야 한다.
enum TeamFisFont {
    static let titleMd = Font.system(size: 20, weight: .semibold)
    static let titleSm = Font.system(size: 17, weight: .semibold)
    static let bodySm = Font.system(size: 14, weight: .medium)
    static let caption = Font.system(size: 12, weight: .regular)
}
