package com.teamfis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.components.MyMenuDivider
import com.teamfis.app.ui.components.MyMenuRow
import com.teamfis.app.ui.components.MySectionHeader
import com.teamfis.app.ui.components.MyStatCard
import com.teamfis.app.ui.components.MyStatDivider
import com.teamfis.app.ui.components.MyStatRow
import com.teamfis.app.ui.components.MyVersionNote
import com.teamfis.app.ui.components.TrainerHeader
import com.teamfis.app.ui.components.TrainerProfile
import com.teamfis.app.ui.components.comma
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing

/**
 * 마이 — 헤더 오른쪽 끝의 사람 아이콘을 누르면 들어온다.
 *
 * **트레이너 자신에 대한 자리다.** 나머지 네 탭이 전부 회원과 수업이라 `나` 를
 * 볼 데가 없었다.
 *
 * 짜임은 대표가 준 마이페이지를 따랐다 (2026-09-09) — **큰 인사말 위에 지점 한 줄,
 * 아래는 아이콘 달린 메뉴 줄**이고 갈래가 바뀌는 자리에만 선을 긋는다.
 *
 * 순서는 **나 → 번 것 → 한 것 → 설정**이다. 이번 달 실적이 위에 오는 것은
 * 트레이너가 이 화면을 여는 이유가 대개 그 숫자여서다.
 *
 * **내 몫만 브랜드 색이다.** 매출은 가게 것이고 그 밑이 내 것이라, 사이에 선을
 * 하나 긋고 무게를 달리 준다.
 *
 * 홈과 겹칠 수 있다 — 홈은 **오늘 할 일**, 여기는 **나에 대한 것**으로 가른다.
 *
 * **잎 화면이다** — 셸의 `NavHost` 가 오른쪽에서 밀어 넣어 하단 탭 바까지 덮는다.
 */
@Composable
fun MyScreen(onBack: () -> Unit) {
    val profile = remember { placeholderProfile }

    Column(
        Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        DetailHeader(title = "마이", onBack = onBack)

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = TeamFisSpacing.screenHorizontal),
        ) {
            TrainerHeader(profile, modifier = Modifier.padding(top = TeamFisSpacing.sm))

            MySectionHeader(
                "이번 달",
                modifier = Modifier.padding(top = TeamFisSpacing.xxxl),
            )
            MyStatCard {
                MyStatRow("신규", "${profile.newMembers}건")
                MyStatRow("재등록", "${profile.renewals}건")
                MyStatRow("매출", "${comma(profile.revenue)}원")
                MyStatDivider()
                // TODO(서버): 요율은 지점이 정한다 — 금액을 받아서 그대로 적는다
                MyStatRow("내 인센티브", "${comma(profile.incentive)}원", highlight = true)
            }

            MySectionHeader("수업", modifier = Modifier.padding(top = TeamFisSpacing.xxxl))
            MyStatCard {
                MyStatRow("진행", "${profile.doneRounds}회차")
                MyStatRow("노쇼", "${profile.noShowRounds}회차")
            }

            Column(Modifier.padding(top = TeamFisSpacing.xxl)) {
                // TODO: 설정 화면들이 붙으면 연결한다
                MyMenuRow(R.drawable.ic_header_notification, "알림 설정", onClick = {})
                MyMenuRow(R.drawable.ic_setting, "앱 설정", onClick = {})

                MyMenuDivider()

                MyMenuRow(R.drawable.ic_logout, "로그아웃", onClick = {}, danger = true)
            }

            MyVersionNote(
                "버전 0.1.0",
                modifier = Modifier.padding(
                    top = TeamFisSpacing.xl,
                    bottom = TeamFisSpacing.xxxl,
                ),
            )
        }
    }
}

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 내 실적을 주면 통째로 걷어낸다.
 * 이름은 다른 화면과 같이 아직 `000` 이다.
 */
private val placeholderProfile = TrainerProfile(
    name = "000",
    branch = "000점",
    phone = "010-0000-0000",
    newMembers = 3,
    renewals = 5,
    revenue = 12_400_000,
    incentive = 2_480_000,
    doneRounds = 48,
    noShowRounds = 2,
)
