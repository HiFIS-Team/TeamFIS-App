package com.teamfis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teamfis.app.ui.components.NotificationKind
import com.teamfis.app.ui.components.NotificationRetentionNote
import com.teamfis.app.ui.components.NotificationRow
import com.teamfis.app.ui.components.TeamFisNotification
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 알림함 — 놓친 알림을 확인한다.
 *
 * 헤더의 알림 아이콘에서 **오른쪽에서 왼쪽으로 밀려 들어온다** (셸의 `NavHost` 가 그린다).
 * 탭 목적지가 아니라 잎 화면이라 하단 탭 바까지 통째로 덮는다.
 */
@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    items: List<TeamFisNotification> = placeholderNotifications,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        DetailHeader(title = "알림", onBack = onBack)

        if (items.isEmpty()) EmptyState() else NotificationList(items)
    }
}

@Composable
private fun NotificationList(items: List<TeamFisNotification>) {
    val unread = items.filter { it.isUnread }
    val read = items.filterNot { it.isUnread }

    LazyColumn(Modifier.fillMaxSize()) {
        // 안 읽은 알림은 **한 덩어리로 밝게 깐다.** 점을 하나씩 찍는 것보다
        // "여기까지가 새 거" 가 한눈에 들어온다
        if (unread.isNotEmpty()) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(TeamFisColor.Surface1)
                        .padding(vertical = TeamFisSpacing.sm),
                ) {
                    unread.forEach { NotificationRow(it) }
                }
            }
        }

        if (read.isNotEmpty()) {
            item {
                Text(
                    "지난 알림",
                    style = TeamFisType.titleMd,
                    color = TeamFisColor.TextPrimary,
                    modifier = Modifier.padding(
                        start = TeamFisSpacing.screenHorizontal,
                        end = TeamFisSpacing.screenHorizontal,
                        top = TeamFisSpacing.xxl,
                        bottom = TeamFisSpacing.sm,
                    ),
                )
            }
            items(read, key = { it.id }) { NotificationRow(it) }
        }

        item { NotificationRetentionNote(RetentionDays) }
        item { Spacer(Modifier.height(TeamFisSpacing.xxxl)) }
    }
}

/** 빈 상태 — 한 줄이면 된다. 갈 곳(알림 설정)이 아직 없어 버튼을 두지 않는다. */
@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("알림이 없어요", style = TeamFisType.titleMd, color = TeamFisColor.TextSecondary)
    }
}

/** TODO(서버): 보관 기간은 서버 정책을 따른다 */
private const val RetentionDays = 7

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 알림을 주면 통째로 걷어낸다.
 * 회원 이름은 아직 다 `000` 이다.
 */
private val placeholderNotifications = listOf(
    TeamFisNotification(
        1, NotificationKind.Class,
        "000 회원님이 수업을 예약했어요",
        "오늘 오후 2:00 · PT",
        "10분 전", isUnread = true,
    ),
    TeamFisNotification(
        2, NotificationKind.NoShow,
        "000 회원님이 오지 않았어요",
        "어제 오후 6:30 수업 · 아직 처리하지 않았습니다",
        "1시간 전", isUnread = true,
    ),
    TeamFisNotification(
        3, NotificationKind.MemberChange,
        "새 회원이 배정됐어요",
        "000 회원님 · 얼리버드 20회",
        "3시간 전", isUnread = true,
    ),
    TeamFisNotification(
        4, NotificationKind.Log,
        "작성하지 않은 일지가 있어요",
        "9/5 · 9/6 수업",
        "어제", isUnread = false, count = 3,
    ),
    TeamFisNotification(
        5, NotificationKind.Class,
        "000 회원님이 수업을 변경했어요",
        "9/9 오후 4:00 → 오후 7:00",
        "어제", isUnread = false,
    ),
    TeamFisNotification(
        6, NotificationKind.MemberChange,
        "000 회원님 회원권이 3회 남았어요",
        "얼리버드 20회 · 17/20회차",
        "3일 전", isUnread = false,
    ),
    TeamFisNotification(
        7, NotificationKind.Notice,
        "추석 연휴 운영 안내",
        "9월 14일 ~ 16일 · 10:00 ~ 18:00",
        "5일 전", isUnread = false,
    ),
)
