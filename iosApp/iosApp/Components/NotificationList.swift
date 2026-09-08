import SwiftUI

/// 알림 한 건.
///
/// TODO(서버): 알림 API 가 아직 없다. 화면을 먼저 세우려고 자리값을 둔다.
struct TeamFisNotification: Identifiable, Hashable {
    let id: Int
    let kind: NotificationKind
    let title: String
    let body: String
    /// TODO(서버): 서버가 주는 시각으로 상대 시간을 계산한다. 지금은 문자열
    let time: String
    let isUnread: Bool
    /// 같은 종류가 여러 건 묶였을 때만. 한 건이면 `nil`
    var count: Int?
}

/// 알림 종류 — **트레이너가 놓치면 안 되는 것들**로 가른다.
///
/// 종류마다 색이 다르다. 브랜드 레드는 여기 끼지 않는다 —
/// 액션 색이 갈래 하나를 맡으면 그 갈래만 눌러야 할 것처럼 보인다.
enum NotificationKind: Hashable {
    /// 수업 예약·변경·취소
    case classChange
    /// 회원 배정·재등록·잔여 회차
    case member
    /// 미작성 일지
    case log
    /// 노쇼
    case noShow
    /// 센터 공지
    case notice

    /// 행 아이콘. **본문 안의 콘텐츠라 두 플랫폼이 같은 벡터를 쓴다**
    var icon: String {
        switch self {
        case .classChange: "ic_calendar"
        case .member: "ic_tab_member_fill"
        case .log: "ic_tab_log_fill"
        case .noShow: "ic_clock"
        case .notice: "ic_header_notification"
        }
    }

    var color: Color {
        switch self {
        case .classChange: TeamFisColor.categoryBlue
        case .member: TeamFisColor.categoryGreen
        case .log: TeamFisColor.categoryViolet
        case .noShow: TeamFisColor.categoryCoral
        case .notice: TeamFisColor.categoryGray
        }
    }
}

/// 알림 한 행.
///
/// 왼쪽 아이콘 타일 · 제목 · 본문 · 오른쪽 위 시각. **구분선은 두지 않는다** —
/// 행마다 선을 그으면 목록이 표처럼 보이고, 묶음(안 읽음 블록)이 안 읽힌다.
struct NotificationRow: View {
    let item: TeamFisNotification

    private static let tile: CGFloat = 44

    var body: some View {
        HStack(alignment: .top, spacing: TeamFisSpacing.md) {
            Image(item.kind.icon)
                .renderingMode(.template)
                .resizable()
                .frame(width: 22, height: 22)
                .foregroundStyle(item.kind.color)
                .frame(width: Self.tile, height: Self.tile)
                // 배경은 같은 색을 옅게 깔아 **타일 자체가 튀지는 않게** 한다
                .background(
                    item.kind.color.opacity(0.16),
                    in: RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                )

            VStack(alignment: .leading, spacing: 2) {
                HStack(alignment: .top, spacing: TeamFisSpacing.sm) {
                    Text(item.title)
                        .font(TeamFisFont.titleSm)
                        .foregroundStyle(TeamFisColor.textPrimary)
                        .lineLimit(1)
                    Spacer(minLength: 0)
                    Text(item.time)
                        .font(TeamFisFont.caption)
                        .foregroundStyle(TeamFisColor.textTertiary)
                        .padding(.top, 2)
                }

                // 건수 배지는 **본문 첫 줄 오른쪽**에 붙인다. 본문은 그 아래로 흘러내린다
                HStack(alignment: .top, spacing: TeamFisSpacing.sm) {
                    Text(item.body)
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(TeamFisColor.textSecondary)
                        .lineLimit(2)
                        .fixedSize(horizontal: false, vertical: true)
                    if let count = item.count {
                        Spacer(minLength: 0)
                        // 브랜드 색을 쓰지 않는다 — 건수는 강조할 값이 아니다
                        Text("\(count)건")
                            .font(TeamFisFont.caption.monospacedDigit())
                            .foregroundStyle(TeamFisColor.textSecondary)
                            .padding(.horizontal, TeamFisSpacing.sm)
                            .padding(.vertical, 2)
                            .background(
                                TeamFisColor.surface2,
                                in: RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                            )
                    }
                }
            }
        }
        .multilineTextAlignment(.leading)
        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
        .padding(.vertical, TeamFisSpacing.md)
        .frame(minHeight: TeamFisSize.listRowMin, alignment: .top)
        // TODO: 알림이 가리키는 화면이 붙으면 행을 눌러 이동한다.
        // 지금 눌러도 갈 곳이 없어 일부러 반응을 넣지 않았다
    }
}

/// 목록 끝의 보관 기간 안내.
///
/// **선 사이에 글을 앉힌다** — 목록이 여기서 끝났다는 걸 알려 주면서
/// "왜 옛날 알림이 없지" 라는 질문을 미리 막는다.
struct NotificationRetentionNote: View {
    let days: Int

    var body: some View {
        HStack(spacing: TeamFisSpacing.md) {
            line
            Text("\(days)일 전 알림까지 확인할 수 있어요")
                .font(TeamFisFont.caption)
                .foregroundStyle(TeamFisColor.textTertiary)
                .fixedSize()
            line
        }
        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
        .padding(.vertical, TeamFisSpacing.xxl)
    }

    private var line: some View {
        Rectangle()
            .fill(TeamFisColor.divider)
            .frame(height: 1)
    }
}
