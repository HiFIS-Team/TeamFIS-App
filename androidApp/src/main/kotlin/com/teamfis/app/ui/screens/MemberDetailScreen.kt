package com.teamfis.app.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teamfis.app.ui.components.BodyPart
import com.teamfis.app.ui.components.Member
import com.teamfis.app.ui.components.MemberDetail
import com.teamfis.app.ui.components.MemberProduct
import com.teamfis.app.ui.components.MemberProfile
import com.teamfis.app.ui.components.MemberSession
import com.teamfis.app.ui.components.ProductSelector
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.components.SessionCard
import com.teamfis.app.ui.components.SessionStatus
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing

/**
 * 회원 상세 — 목록에서 한 명을 눌렀을 때.
 *
 * **제목 없는 [DetailHeader]** 를 쓴다 — 이름을 본문에서 크게 다루므로
 * 머리에 또 적으면 같은 말이 두 번이다.
 *
 * 순서는 **사람 → 등록 → 회차**다. 누구인지 알고, 뭘 끊었는지 보고,
 * 그 아래에서 회차를 처리한다.
 *
 * **잎 화면이다** — 셸의 `NavHost` 가 오른쪽에서 밀어 넣어 하단 탭 바까지 덮는다.
 * 셸 밖이라 시스템 바 여백도 스스로 챙긴다. 기기 뒤로가기는 `NavHost` 가 받는다.
 */
@Composable
fun MemberDetailScreen(member: Member, onBack: () -> Unit) {
    val detail = remember(member) { placeholderDetail(member) }
    var productIndex by remember { mutableIntStateOf(0) }
    var productExpanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // 머리는 고정, 아래만 흐른다 (iOS 와 같은 모양)
        DetailHeader(title = null, onBack = onBack)

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            MemberProfile(detail, modifier = Modifier.padding(top = TeamFisSpacing.sm))

            Box(
                Modifier
                    .padding(
                        top = TeamFisSpacing.xl,
                        start = TeamFisSpacing.screenHorizontal,
                        end = TeamFisSpacing.screenHorizontal,
                    )
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TeamFisColor.Divider),
            )

            ProductSelector(
                products = detail.products,
                selectedIndex = productIndex,
                expanded = productExpanded,
                onToggle = { productExpanded = !productExpanded },
                onSelect = {
                    productIndex = it
                    productExpanded = false
                },
                modifier = Modifier.padding(top = TeamFisSpacing.sm),
            )

            Column(
                modifier = Modifier.padding(
                    top = TeamFisSpacing.sm,
                    start = TeamFisSpacing.screenHorizontal,
                    end = TeamFisSpacing.screenHorizontal,
                    bottom = TeamFisSpacing.xxxl,
                ),
                verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                detail.products[productIndex].sessions.forEach { session ->
                    // 회차 처리는 서버가 붙어야 한다
                    SessionCard(session, onNoShow = {}, onDone = {})
                }
            }
        }
    }
}

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 회원 상세를 주면 통째로 걷어낸다.
 * 목록에서 누른 회원의 이름만 이어 받는다.
 */
private fun placeholderDetail(member: Member) = MemberDetail(
    name = member.name,
    gender = "남",
    phone = "010-1234-4564",
    birth = "1999. 12. 12.",
    products = listOf(
        MemberProduct(
            name = "얼리버드 20회",
            sessions = listOf(
                MemberSession(3, "2026.03.21 (토) 10:00", SessionStatus.Scheduled),
                MemberSession(
                    2, "2026.03.18 (수) 19:30", SessionStatus.Done,
                    parts = listOf(
                        BodyPart.Chest, BodyPart.Leg, BodyPart.Back,
                        BodyPart.Arm, BodyPart.Shoulder, BodyPart.Cardio,
                    ),
                ),
                MemberSession(
                    1, "2026.03.14 (토) 10:00", SessionStatus.Done,
                    parts = listOf(BodyPart.Back, BodyPart.Arm),
                ),
            ),
        ),
        MemberProduct(
            name = "PT 30회",
            sessions = listOf(
                MemberSession(
                    30, "2026.02.27 (금) 20:00", SessionStatus.Done,
                    parts = listOf(BodyPart.Leg, BodyPart.Cardio),
                ),
                MemberSession(29, "2026.02.24 (화) 20:00", SessionStatus.NoShow),
            ),
        ),
    ),
)
