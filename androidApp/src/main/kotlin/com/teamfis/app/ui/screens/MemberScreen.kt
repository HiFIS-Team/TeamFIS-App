package com.teamfis.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.teamfis.app.ui.components.Member
import com.teamfis.app.ui.components.MemberDivider
import com.teamfis.app.ui.components.MemberFilterBar
import com.teamfis.app.ui.components.MemberRow
import com.teamfis.app.R
import com.teamfis.app.ui.components.MemberStatus
import com.teamfis.app.ui.components.placeholderMembers
import com.teamfis.app.ui.shell.AppHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 회원.
 *
 * **보유 회원 전체가 기본**이고, 필터는 그 위에서 갈래를 좁힌다.
 * 목록이 수십 줄로 길어지므로 필터 줄은 **위에 고정**한다 — 같이 흘러가면
 * 아래에서 갈래를 바꾸려고 맨 위까지 되돌아가야 한다.
 *
 * 한 명을 누르면 상세가 열리는데, **이 화면이 상세를 그리지 않는다.** 상세는
 * 하단 탭 바까지 덮는 잎 화면이라 셸만 띄울 수 있다 — 여기서는 요청만 한다.
 *
 * 회원 추가는 **안드로이드 표준대로 FAB** 이다 (2026-09-08 대표 지정).
 * iOS 는 FAB 관습이 없고 오른쪽 아래에 다음 수업 유리 줄이 이미 떠 있어서,
 * 거기서는 필터 줄 오른쪽 끝에 붙는다 — **두 플랫폼이 일부러 다르다.**
 */
@Composable
fun MemberScreen(
    onMember: (Member) -> Unit = {},
    onNotification: () -> Unit = {},
    onAddMember: () -> Unit = {},
    onMy: () -> Unit = {},
) {
    var filter by rememberSaveable { mutableStateOf<MemberStatus?>(null) }

    val counts = remember { placeholderMembers.groupingBy { it.status }.eachCount() }
    val shown = remember(filter) {
        filter?.let { status -> placeholderMembers.filter { it.status == status } } ?: placeholderMembers
    }

    Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize()) {
        AppHeader(onNotification = onNotification, onMy = onMy)

        MemberFilterBar(
            selected = filter,
            counts = counts,
            // 고른 것을 다시 누르면 풀려서 전체로 돌아온다
            onSelect = { filter = if (filter == it) null else it },
            modifier = Modifier.padding(
                top = TeamFisSpacing.sm,
                bottom = TeamFisSpacing.md,
            ),
        )

        if (shown.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("회원이 없습니다", style = TeamFisType.bodySm, color = TeamFisColor.TextMuted)
            }
        } else {
            // 마지막 줄이 FAB 에 가리지 않게 그만큼 비운다
            LazyColumn(contentPadding = PaddingValues(bottom = FabClearance)) {
                itemsIndexed(shown) { index, member ->
                    if (index > 0) MemberDivider()
                    MemberRow(member, onClick = { onMember(member) })
                }
            }
        }
    }

        // TODO: 회원 추가 화면이 붙으면 연결한다
        FloatingActionButton(
            onClick = onAddMember,
            containerColor = TeamFisColor.Brand,
            contentColor = TeamFisColor.TextPrimary,
            shape = TeamFisRadius.card,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(TeamFisSpacing.lg),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "회원 추가",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

/** FAB(56) + 위아래 여백. 목록 마지막 줄이 버튼에 가리지 않을 만큼이다. */
private val FabClearance = 88.dp
