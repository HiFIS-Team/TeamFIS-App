package com.teamfis.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.teamfis.app.ui.components.Member
import com.teamfis.app.ui.components.MemberDivider
import com.teamfis.app.ui.components.MemberFilterBar
import com.teamfis.app.ui.components.MemberRow
import com.teamfis.app.ui.components.MemberStatus
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 회원.
 *
 * **보유 회원 전체가 기본**이고, 필터는 그 위에서 갈래를 좁힌다.
 * 목록이 수십 줄로 길어지므로 필터 줄은 **위에 고정**한다 — 같이 흘러가면
 * 아래에서 갈래를 바꾸려고 맨 위까지 되돌아가야 한다.
 */
@Composable
fun MemberScreen() {
    var filter by rememberSaveable { mutableStateOf<MemberStatus?>(null) }

    val counts = remember { placeholderMembers.groupingBy { it.status }.eachCount() }
    val shown = remember(filter) {
        filter?.let { status -> placeholderMembers.filter { it.status == status } } ?: placeholderMembers
    }

    Column(Modifier.fillMaxSize()) {
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
            LazyColumn(contentPadding = PaddingValues(bottom = TeamFisSpacing.xxxl)) {
                itemsIndexed(shown) { index, member ->
                    if (index > 0) MemberDivider()
                    // 회원 상세 화면이 생기면 연결한다
                    MemberRow(member, onClick = {})
                }
            }
        }
    }
}

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 회원 목록을 주면 통째로 걷어낸다.
 * 이름은 아직 다 `000` 이다.
 */
private val placeholderMembers = listOf(
    Member("000", MemberStatus.Active, "12/30회차", "마지막 9/5"),
    Member("000", MemberStatus.Active, "3/20회차", "마지막 9/6"),
    Member("000", MemberStatus.Active, "8/10회차", "마지막 9/4"),
    Member("000", MemberStatus.Active, "27/30회차", "마지막 9/6"),
    Member("000", MemberStatus.Holding, "14/40회차", "9/1부터 홀딩"),
    Member("000", MemberStatus.Active, "1/50회차", "마지막 9/2"),
    Member("000", MemberStatus.Expired, "20/20회차", "8/28 만료"),
    Member("000", MemberStatus.Active, "19/30회차", "마지막 9/3"),
    Member("000", MemberStatus.Holding, "6/20회차", "8/20부터 홀딩"),
    Member("000", MemberStatus.Expired, "30/30회차", "8/11 만료"),
    Member("000", MemberStatus.Active, "5/10회차", "마지막 9/6"),
    Member("000", MemberStatus.Expired, "10/10회차", "7/30 만료"),
)
