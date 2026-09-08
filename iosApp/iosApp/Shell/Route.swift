import SwiftUI

/// 잎 화면 — 셸(탭 + 하단 유리 바)을 **통째로 덮는** 페이지.
///
/// 안드로이드 `Route`(= `NavHost` 에서 `SHELL` 과 형제인 라우트)와 1:1 이다.
/// 탭 안에서 밀리는 화면이 아니라 **셸 위에 얹히는** 화면이라는 뜻이다.
enum Route: Hashable {
    /// 회원 상세 — 회원 목록의 한 줄을 누르면 들어온다
    case memberDetail(Member)
}
