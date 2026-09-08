package com.teamfis.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSize
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 알림 한 건.
 *
 * TODO(서버): 알림 API 가 아직 없다. 화면을 먼저 세우려고 자리값을 둔다.
 */
data class TeamFisNotification(
    val id: Int,
    val kind: NotificationKind,
    val title: String,
    val body: String,
    /** TODO(서버): 서버가 주는 시각으로 상대 시간을 계산한다. 지금은 문자열 */
    val time: String,
    val isUnread: Boolean,
    /** 같은 종류가 여러 건 묶였을 때만. 한 건이면 `null` */
    val count: Int? = null,
)

/**
 * 알림 종류 — **트레이너가 놓치면 안 되는 것들**로 가른다.
 *
 * 종류마다 색이 다르다. 브랜드 레드는 여기 끼지 않는다 —
 * 액션 색이 갈래 하나를 맡으면 그 갈래만 눌러야 할 것처럼 보인다.
 */
enum class NotificationKind(@DrawableRes val icon: Int, val color: Color) {
    /** 수업 예약·변경·취소 */
    Class(R.drawable.ic_calendar, TeamFisColor.CategoryBlue),

    /** 회원 배정·재등록·잔여 회차 */
    MemberChange(R.drawable.ic_tab_member_fill, TeamFisColor.CategoryGreen),

    /** 미작성 일지 */
    Log(R.drawable.ic_tab_log_fill, TeamFisColor.CategoryViolet),

    /** 노쇼 */
    NoShow(R.drawable.ic_clock, TeamFisColor.CategoryCoral),

    /** 센터 공지 */
    Notice(R.drawable.ic_header_notification, TeamFisColor.CategoryGray),
}

/**
 * 알림 한 행.
 *
 * 왼쪽 아이콘 타일 · 제목 · 본문 · 오른쪽 위 시각. **구분선은 두지 않는다** —
 * 행마다 선을 그으면 목록이 표처럼 보이고, 묶음(안 읽음 블록)이 안 읽힌다.
 */
@Composable
fun NotificationRow(item: TeamFisNotification, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = TeamFisSize.listRowMin)
            .padding(
                horizontal = TeamFisSpacing.screenHorizontal,
                vertical = TeamFisSpacing.md,
            ),
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
        // TODO: 알림이 가리키는 화면이 붙으면 행을 눌러 이동한다.
        // 지금 눌러도 갈 곳이 없어 일부러 반응을 넣지 않았다
    ) {
        // 배경은 같은 색을 옅게 깔아 **타일 자체가 튀지는 않게** 한다
        Box(
            Modifier
                .size(TileSize)
                .clip(TeamFisRadius.card)
                .background(item.kind.color.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(item.kind.icon),
                contentDescription = null, // 옆 제목이 이름 역할을 한다
                tint = item.kind.color,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    item.title,
                    style = TeamFisType.titleSm,
                    color = TeamFisColor.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    item.time,
                    style = TeamFisType.caption,
                    color = TeamFisColor.TextTertiary,
                    modifier = Modifier.padding(start = TeamFisSpacing.sm, top = 2.dp),
                )
            }

            // 건수 배지는 **본문 첫 줄 오른쪽**에 붙인다. 본문은 그 아래로 흘러내린다
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    item.body,
                    style = TeamFisType.bodySm,
                    color = TeamFisColor.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                item.count?.let { count ->
                    Spacer(Modifier.weight(1f))
                    // 브랜드 색을 쓰지 않는다 — 건수는 강조할 값이 아니다
                    Text(
                        "${count}건",
                        style = TeamFisType.caption.copy(fontFeatureSettings = "tnum"),
                        color = TeamFisColor.TextSecondary,
                        modifier = Modifier
                            .padding(start = TeamFisSpacing.sm)
                            .background(TeamFisColor.Surface2, TeamFisRadius.card)
                            .padding(horizontal = TeamFisSpacing.sm, vertical = 2.dp),
                    )
                }
            }
        }
    }
}

/**
 * 목록 끝의 보관 기간 안내.
 *
 * **선 사이에 글을 앉힌다** — 목록이 여기서 끝났다는 걸 알려 주면서
 * "왜 옛날 알림이 없지" 라는 질문을 미리 막는다.
 */
@Composable
fun NotificationRetentionNote(days: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.screenHorizontal, vertical = TeamFisSpacing.xxl),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Line(Modifier.weight(1f))
        Text(
            "${days}일 전 알림까지 확인할 수 있어요",
            style = TeamFisType.caption,
            color = TeamFisColor.TextTertiary,
            modifier = Modifier.padding(horizontal = TeamFisSpacing.md),
        )
        Line(Modifier.weight(1f))
    }
}

@Composable
private fun Line(modifier: Modifier) {
    Box(
        modifier
            .height(1.dp)
            .background(TeamFisColor.Divider),
    )
}

private val TileSize = 44.dp
