package com.teamfis.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisMotion
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.LocalDateTime

/** 회원 상세 — 목록에서 한 명을 눌렀을 때 펼쳐지는 것. */
data class MemberDetail(
    val name: String,
    /** `남` · `여` — 이름 옆 괄호에 들어간다 */
    val gender: String,
    val phone: String,
    val birth: String,
    /** 등록 때 받은 값 — 어떻게 알고 왔나 */
    val visitPath: VisitPath?,
    /** 등록 때 받은 값 — 데려온 회원. 없으면 null */
    val referrer: String?,
    /** 등록 상품. 여러 개면 위 줄에서 골라 가며 본다 */
    val products: List<MemberProduct>,
)

/**
 * 등록 상품 하나 — 회차가 이 밑에 달린다.
 *
 * **결제액·회차는 사람이 아니라 여기 붙는다.** 회원 하나가 등록권을 여럿 들 수 있어서,
 * 프로필에 결제액을 적으면 어느 등록권 것인지 알 수 없다.
 */
data class MemberProduct(
    val name: String,
    /** 등록 회차 */
    val rounds: Int,
    /** 결제액 (원) */
    val payment: Int,
    /** `2026. 3. 10.` */
    val registeredAt: String,
    val sessions: List<MemberSession>,
) {
    /** 회당 단가 — 등록 화면과 같은 셈이다 (결제액 ÷ 회차) */
    val unitPrice: Int
        get() = if (rounds > 0 && payment > 0) Math.round(payment.toDouble() / rounds).toInt() else 0
}

/** `1,234,567` */
fun comma(value: Int): String =
    value.toString().reversed().chunked(3).joinToString(",").reversed()

/**
 * 회차 한 건.
 *
 * **시각을 글자가 아니라 값으로 든다** — 회차를 누르면 그 회차의 일지로 가는데,
 * 일지 화면이 날짜를 값으로 받아야 해서다 (`ScheduleClass` 와 같은 이유).
 */
data class MemberSession(
    val round: Int,
    val at: LocalDateTime,
    val status: SessionStatus,
    /** 수업이 끝난 회차만 채워진다 */
    val parts: List<BodyPart> = emptyList(),
    /**
     * 회원에게 세션 사인까지 받았나.
     *
     * **일지를 썼어도 사인 전이면 아직 안 닫힌 회차다.** 목록에서는 그때까지 펴지 않는다.
     */
    val signed: Boolean = false,
) {
    /** `2026.03.21 (토) 10:00` — 카드에 서는 글자 */
    val atLabel: String
        get() = "%04d.%02d.%02d (%s) %02d:%02d".format(
            at.year, at.monthValue, at.dayOfMonth,
            at.dayOfWeek.koLabel, at.hour, at.minute,
        )
}

/** 회차 상태. */
enum class SessionStatus(val label: String) {
    Scheduled("수업예정"),
    Done("수업완료"),
    NoShow("노쇼"),
    ;

    /** 목록에서 상태를 훑을 때 쓰는 점 색 */
    val dotColor: Color
        get() = when (this) {
            Scheduled -> TeamFisColor.Scheduled
            Done -> TeamFisColor.TextTertiary
            NoShow -> TeamFisColor.Brand
        }
}

/** 그날 한 운동 부위. 칩에 아이콘과 함께 붙는다. */
enum class BodyPart(val label: String, @DrawableRes val icon: Int) {
    Chest("CHEST", R.drawable.ic_part_chest),
    Leg("LEG", R.drawable.ic_part_leg),
    Back("BACK", R.drawable.ic_part_back),
    Arm("ARM", R.drawable.ic_part_arm),
    Shoulder("SHOULDER", R.drawable.ic_part_shoulder),
    Cardio("CARDIO", R.drawable.ic_part_cardio),
}

/**
 * 이름 줄 + 연락처 줄.
 *
 * 이름이 이 화면에서 제일 큰 글자다. 전화는 **이름 줄 오른쪽 끝**에 둔다 —
 * 번호를 눈으로 읽고 손으로 옮겨 적는 일이 없어야 한다.
 */
@Composable
fun MemberProfile(member: MemberDetail, onCall: () -> Unit = {}, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = TeamFisSpacing.screenHorizontal,
                    end = TeamFisSpacing.screenHorizontal - (TouchTarget - Icon24) / 2,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${member.name} (${member.gender})",
                style = TeamFisType.titleLg,
                color = TeamFisColor.TextPrimary,
            )

            Spacer(Modifier.weight(1f))

            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(TouchTarget)
                    .clickable(interactionSource = interaction, indication = null, onClick = onCall),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_call),
                    contentDescription = "전화 걸기",
                    tint = TeamFisColor.TextPrimary,
                    modifier = Modifier.size(Icon24),
                )
            }
        }

        Column(
            modifier = Modifier.padding(
                top = TeamFisSpacing.xl,
                start = TeamFisSpacing.screenHorizontal,
                end = TeamFisSpacing.screenHorizontal,
            ),
            verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.md),
        ) {
            DetailInfoRow("휴대폰 번호", member.phone)
            DetailInfoRow("생년월일", member.birth)
            // 등록 때 받은 값 중 **사람에 붙는 것**만 여기 온다
            DetailInfoRow("방문 경로", member.visitPath?.label ?: "—")
            DetailInfoRow("소개한 회원", member.referrer?.let { "$it 회원님" } ?: "—")
        }
    }
}

/** 이름표는 왼쪽, 값은 오른쪽 끝. 프로필과 등록권이 같이 쓴다. */
@Composable
fun DetailInfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = TeamFisType.bodySm, color = TeamFisColor.TextTertiary)
        Spacer(Modifier.weight(1f))
        Text(
            value,
            // 번호·날짜라 자릿수가 바뀌어도 오른쪽 끝이 안 흔들려야 한다
            style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
            color = TeamFisColor.TextSecondary,
        )
    }
}

/**
 * 등록 상품 고르는 줄 — `얼리버드 20회 ˅`.
 *
 * 상품이 하나뿐이면 **화살표를 안 그린다.** 눌러도 아무 일 없는 것을 두면
 * 누를 수 있는 줄 알고 누르게 된다.
 */
@Composable
fun ProductSelector(
    products: List<MemberProduct>,
    selectedIndex: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val single = products.size <= 1
    val arrow by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = TeamFisMotion.base(),
        label = "productArrow",
    )

    Column(modifier.fillMaxWidth()) {
        val interaction = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (single) Modifier
                    else Modifier.clickable(
                        interactionSource = interaction,
                        indication = null,
                        onClick = onToggle,
                    ),
                )
                .padding(
                    horizontal = TeamFisSpacing.screenHorizontal,
                    vertical = TeamFisSpacing.md,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.xs),
        ) {
            Text(
                products[selectedIndex].name,
                style = TeamFisType.titleSm,
                color = TeamFisColor.TextPrimary,
            )
            if (!single) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_down),
                    contentDescription = null, // 옆 글자가 이름 역할을 한다
                    tint = TeamFisColor.TextSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer { rotationZ = arrow },
                )
            }
        }

        if (expanded && !single) {
            products.forEachIndexed { index, product ->
                if (index == selectedIndex) return@forEachIndexed
                val rowInteraction = remember(index) { MutableInteractionSource() }
                Text(
                    product.name,
                    style = TeamFisType.bodySm,
                    color = TeamFisColor.TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = rowInteraction,
                            indication = null,
                            onClick = { onSelect(index) },
                        )
                        .padding(
                            horizontal = TeamFisSpacing.screenHorizontal,
                            vertical = TeamFisSpacing.md,
                        ),
                )
            }
        }
    }
}

/**
 * 회차 카드 한 장.
 *
 * **끝난 회차와 안 끝난 회차의 아래가 다르다** — 예정이면 처리 버튼 둘,
 * 끝났으면 그날 한 운동 부위. 같은 자리에 다른 것이 오므로 카드 높이도 달라진다.
 */
@Composable
fun SessionCard(
    session: MemberSession,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(TeamFisRadius.card)
            .background(TeamFisColor.Surface1)
            // 누르면 그 회차의 일지로 간다 (HiFIS 와 같은 길)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(TeamFisSpacing.lg),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${session.round}회차",
                style = TeamFisType.titleSm,
                color = TeamFisColor.TextPrimary,
            )
            Spacer(Modifier.weight(1f))
            SessionBadge(session.status)
        }

        Row(
            modifier = Modifier.padding(top = TeamFisSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
        ) {
            Text(
                session.atLabel,
                style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
                color = TeamFisColor.TextSecondary,
            )
            if (session.status == SessionStatus.Scheduled) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = null, // 옆 날짜가 이름 역할을 한다
                    tint = TeamFisColor.TextTertiary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        // **사인까지 받은 회차만 편다** (2026-09-08 대표 지시).
        // 여기는 회차를 훑는 자리라 아직 안 닫힌 것은 그냥 카드로 둔다 —
        // 처리(노쇼·완료)도 일정에서 연 수업 상세 한 곳에서만 한다
        if (session.signed && session.parts.isNotEmpty()) {
            BodyPartChips(
                parts = session.parts,
                modifier = Modifier.padding(top = TeamFisSpacing.md),
            )
        }
    }
}

/**
 * 회차 처리 버튼 — 둘이 **같은 폭**이다. 하나가 넓으면 그쪽이 정답처럼 보인다.
 *
 * 수업 상세도 같은 버튼을 쓴다. 같은 일을 하는 버튼이 두 모양이면 안 된다.
 */
@Composable
fun RowScope.SessionButton(
    label: String,
    background: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .weight(1f)
            .height(ButtonHeight)
            .clip(TeamFisRadius.card)
            .background(background)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = TeamFisType.bodySm, color = contentColor)
    }
}

/** 상태 배지 — 예정만 초록으로 찬다. 나머지는 지난 일이라 가라앉힌다. */
@Composable
fun SessionBadge(status: SessionStatus) {
    val filled = status == SessionStatus.Scheduled
    Text(
        status.label,
        style = TeamFisType.caption,
        color = if (filled) TeamFisColor.TextPrimary else TeamFisColor.TextTertiary,
        modifier = Modifier
            .background(
                if (filled) TeamFisColor.Scheduled else TeamFisColor.Surface2,
                TeamFisRadius.card,
            )
            .padding(horizontal = TeamFisSpacing.sm, vertical = 3.dp),
    )
}

/**
 * 그날 한 운동 부위 — 아이콘 + 대문자 이름.
 *
 * 여섯 개까지 붙어 한 줄에 안 들어가므로 **넘치면 다음 줄로 흘린다.**
 * 가로 스크롤로 만들면 뒤쪽 부위가 숨어서 그날 뭘 했는지 한눈에 안 보인다.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BodyPartChips(parts: List<BodyPart>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm),
    ) {
        parts.forEach { part ->
            Row(
                modifier = Modifier
                    .background(TeamFisColor.Surface2, TeamFisRadius.card)
                    .padding(horizontal = TeamFisSpacing.sm, vertical = TeamFisSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TeamFisSpacing.xs),
            ) {
                Icon(
                    painter = painterResource(part.icon),
                    contentDescription = null, // 옆 글자가 이름 역할을 한다
                    tint = TeamFisColor.TextSecondary,
                    modifier = Modifier.size(PartIcon),
                )
                Text(part.label, style = TeamFisType.caption, color = TeamFisColor.TextSecondary)
            }
        }
    }
}

private val TouchTarget = 44.dp
private val Icon24 = 24.dp
private val ButtonHeight = 48.dp
private val PartIcon = 14.dp

/**
 * 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 회원 상세를 주면 통째로 걷어낸다.
 * 목록에서 누른 회원의 이름만 이어 받는다.
 * 회원 상세와 일지 화면이 **같은 회원을 그리므로** 여기 한 벌만 둔다.
 */
fun placeholderMemberDetail(member: Member) = MemberDetail(
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
                MemberSession(3, LocalDateTime.of(2026, 3, 21, 10, 0), SessionStatus.Scheduled),
                MemberSession(
                    2, LocalDateTime.of(2026, 3, 18, 19, 30), SessionStatus.Done,
                    parts = listOf(
                        BodyPart.Chest, BodyPart.Leg, BodyPart.Back,
                        BodyPart.Arm, BodyPart.Shoulder, BodyPart.Cardio,
                    ),
                    signed = true,
                ),
                // 일지는 썼는데 사인을 아직 못 받은 회차 — 그냥 카드로 선다
                MemberSession(
                    1, LocalDateTime.of(2026, 3, 14, 10, 0), SessionStatus.Done,
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
                    30, LocalDateTime.of(2026, 2, 27, 20, 0), SessionStatus.Done,
                    parts = listOf(BodyPart.Leg, BodyPart.Cardio),
                    signed = true,
                ),
                MemberSession(29, LocalDateTime.of(2026, 2, 24, 20, 0), SessionStatus.NoShow),
            ),
        ),
    ),
)
