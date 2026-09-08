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
import com.teamfis.app.R
import com.teamfis.app.ui.components.BodyPart
import com.teamfis.app.ui.components.DetailInfoRow
import com.teamfis.app.ui.components.comma
import com.teamfis.app.ui.components.Member
import com.teamfis.app.ui.components.MemberDetail
import com.teamfis.app.ui.components.MemberProduct
import com.teamfis.app.ui.components.MemberProfile
import com.teamfis.app.ui.components.MemberSession
import com.teamfis.app.ui.components.ProductSelector
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.components.SessionCard
import com.teamfis.app.ui.components.SessionStatus
import com.teamfis.app.ui.components.VisitPath
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing

/**
 * 회원 상세 — 목록에서 한 명을 눌렀을 때.
 *
 * 머리에 **회원 이름**이 선다 (알림함과 같은 자리). 잎 화면은 자기가 어디인지 밝혀야 하고,
 * 회원이 여럿일 때 지금 누구를 보고 있는지가 스크롤을 내려도 남아 있어야 한다.
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
        DetailHeader(
            title = "${detail.name} 회원님",
            onBack = onBack,
            // TODO: 회원 설정 화면이 붙으면 연결한다
            actionIcon = R.drawable.ic_setting,
            actionDescription = "회원 설정",
        )

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

            // **이 등록권에 붙는 값** — 상품을 바꾸면 같이 바뀐다.
            // 결제액을 프로필에 적으면 등록권이 여럿일 때 어느 것인지 알 수 없다
            val product = detail.products[productIndex]
            Column(
                modifier = Modifier.padding(
                    top = TeamFisSpacing.sm,
                    start = TeamFisSpacing.screenHorizontal,
                    end = TeamFisSpacing.screenHorizontal,
                ),
                verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                DetailInfoRow("등록일", product.registeredAt)
                DetailInfoRow("결제액", "${comma(product.payment)}원")
                DetailInfoRow(
                    "회당 단가",
                    if (product.unitPrice > 0) "${comma(product.unitPrice)}원" else "—",
                )
            }

            Column(
                modifier = Modifier.padding(
                    top = TeamFisSpacing.xl,
                    start = TeamFisSpacing.screenHorizontal,
                    end = TeamFisSpacing.screenHorizontal,
                    bottom = TeamFisSpacing.xxxl,
                ),
                verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
            ) {
                product.sessions.forEach { session -> SessionCard(session) }
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
    visitPath = VisitPath.Referral,
    referrer = "000",
    products = listOf(
        MemberProduct(
            name = "얼리버드 20회",
            rounds = 20,
            payment = 1_500_000,
            registeredAt = "2026. 3. 10.",
            sessions = listOf(
                MemberSession(3, "2026.03.21 (토) 10:00", SessionStatus.Scheduled),
                MemberSession(
                    2, "2026.03.18 (수) 19:30", SessionStatus.Done,
                    parts = listOf(
                        BodyPart.Chest, BodyPart.Leg, BodyPart.Back,
                        BodyPart.Arm, BodyPart.Shoulder, BodyPart.Cardio,
                    ),
                    signed = true,
                ),
                // 일지는 썼는데 사인을 아직 못 받은 회차 — 그냥 카드로 선다
                MemberSession(
                    1, "2026.03.14 (토) 10:00", SessionStatus.Done,
                    parts = listOf(BodyPart.Back, BodyPart.Arm),
                ),
            ),
        ),
        MemberProduct(
            name = "PT 30회",
            rounds = 30,
            payment = 2_100_000,
            registeredAt = "2025. 11. 2.",
            sessions = listOf(
                MemberSession(
                    30, "2026.02.27 (금) 20:00", SessionStatus.Done,
                    parts = listOf(BodyPart.Leg, BodyPart.Cardio),
                    signed = true,
                ),
                MemberSession(29, "2026.02.24 (화) 20:00", SessionStatus.NoShow),
            ),
        ),
    ),
)
