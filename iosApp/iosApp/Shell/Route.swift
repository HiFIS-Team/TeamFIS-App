import SwiftUI

/// 잎 화면 — 셸(탭 + 하단 유리 바)을 **통째로 덮는** 페이지.
///
/// 안드로이드 `Route`(= `NavHost` 에서 `SHELL` 과 형제인 라우트)와 1:1 이다.
/// 탭 안에서 밀리는 화면이 아니라 **셸 위에 얹히는** 화면이라는 뜻이다.
enum Route: Hashable {
    /// 회원 상세 — 회원 목록의 한 줄을 누르면 들어온다
    case memberDetail(Member)
    /// 수업 상세 — 일정의 수업 카드를 누르면 들어온다
    case scheduleDetail(ScheduleClass)
    /// 회원 일지 — 수업 탭의 일지 목록에서 회원 하나를 누르면 들어온다
    case classMember(Member)
    /// 일지 작성 — 회원 일지에서 회차 하나를 누르면 들어온다
    case classLog(ClassTodo)
    /// 세션 사인 — 수업 탭의 사인 목록에서 한 건을 누르면 들어온다
    case classSign(ClassTodo)
    /// 마이 — 헤더의 사람 아이콘을 누르면 들어온다
    case my
    /// 알림함 — 헤더의 종을 누르면 들어온다
    case notifications
    /// 회원 등록 — 회원 목록의 `+` 로 들어온다
    case memberRegister
    /// 소개한 회원 고르기 — 등록 화면 위에 한 겹 더 얹힌다
    case referrerPick
    /// 재등록할 회원 고르기 — 소개한 회원과 같은 화면을 쓴다
    case renewMemberPick
}
