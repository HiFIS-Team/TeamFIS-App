import SwiftUI

/// 알림함 — 놓친 알림을 확인한다.
///
/// 헤더의 알림 아이콘에서 **오른쪽에서 왼쪽으로 밀려 들어와 셸을 통째로 덮는다.**
/// 하단 유리 바도 그 아래로 가려진다 — 감추는 게 아니라 덮이는 것이다.
struct NotificationScreen: View {
    let onBack: () -> Void
    var items: [TeamFisNotification] = Self.placeholder

    /// TODO(서버): 보관 기간은 서버 정책을 따른다
    private static let retentionDays = 7

    var body: some View {
        VStack(spacing: 0) {
            DetailHeader(title: "알림", onBack: onBack)

            if items.isEmpty {
                emptyState
            } else {
                list
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
    }

    private var list: some View {
        let unread = items.filter(\.isUnread)
        let read = items.filter { !$0.isUnread }

        return ScrollView {
            LazyVStack(alignment: .leading, spacing: 0) {
                // 안 읽은 알림은 **한 덩어리로 밝게 깐다.** 점을 하나씩 찍는 것보다
                // "여기까지가 새 거" 가 한눈에 들어온다
                if !unread.isEmpty {
                    VStack(spacing: 0) {
                        ForEach(unread) { NotificationRow(item: $0) }
                    }
                    .padding(.vertical, TeamFisSpacing.sm)
                    .frame(maxWidth: .infinity)
                    .background(TeamFisColor.surface1)
                }

                if !read.isEmpty {
                    Text("지난 알림")
                        .font(TeamFisFont.titleMd)
                        .foregroundStyle(TeamFisColor.textPrimary)
                        .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                        .padding(.top, TeamFisSpacing.xxl)
                        .padding(.bottom, TeamFisSpacing.sm)

                    ForEach(read) { NotificationRow(item: $0) }
                }

                NotificationRetentionNote(days: Self.retentionDays)
            }
            .padding(.bottom, TeamFisSpacing.xxxl)
        }
    }

    /// 빈 상태 — 한 줄이면 된다. 갈 곳(알림 설정)이 아직 없어 버튼을 두지 않는다.
    private var emptyState: some View {
        VStack {
            Spacer()
            Text("알림이 없어요")
                .font(TeamFisFont.titleMd)
                .foregroundStyle(TeamFisColor.textSecondary)
            Spacer()
        }
    }

    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 알림을 주면 통째로 걷어낸다.
    /// 회원 이름은 아직 다 `000` 이다.
    private static let placeholder: [TeamFisNotification] = [
        .init(id: 1, kind: .classChange,
              title: "000 회원님이 수업을 예약했어요",
              body: "오늘 오후 2:00 · PT",
              time: "10분 전", isUnread: true),
        .init(id: 2, kind: .noShow,
              title: "000 회원님이 오지 않았어요",
              body: "어제 오후 6:30 수업 · 아직 처리하지 않았습니다",
              time: "1시간 전", isUnread: true),
        .init(id: 3, kind: .member,
              title: "새 회원이 배정됐어요",
              body: "000 회원님 · 얼리버드 20회",
              time: "3시간 전", isUnread: true),
        .init(id: 4, kind: .log,
              title: "작성하지 않은 일지가 있어요",
              body: "9/5 · 9/6 수업",
              time: "어제", isUnread: false, count: 3),
        .init(id: 5, kind: .classChange,
              title: "000 회원님이 수업을 변경했어요",
              body: "9/9 오후 4:00 → 오후 7:00",
              time: "어제", isUnread: false),
        .init(id: 6, kind: .member,
              title: "000 회원님 회원권이 3회 남았어요",
              body: "얼리버드 20회 · 17/20회차",
              time: "3일 전", isUnread: false),
        .init(id: 7, kind: .notice,
              title: "추석 연휴 운영 안내",
              body: "9월 14일 ~ 16일 · 10:00 ~ 18:00",
              time: "5일 전", isUnread: false),
    ]
}
