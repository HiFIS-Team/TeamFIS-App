package com.teamfis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.teamfis.app.R
import com.teamfis.app.ui.components.BottomActionButton
import com.teamfis.app.ui.components.ChoiceChips
import com.teamfis.app.ui.components.FieldLabel
import com.teamfis.app.ui.components.FormField
import com.teamfis.app.ui.components.Member
import com.teamfis.app.ui.components.PickerField
import com.teamfis.app.ui.components.SegmentedTabs
import com.teamfis.app.ui.components.ToggleRow
import com.teamfis.app.ui.components.VisitPath
import com.teamfis.app.ui.components.placeholderMembers
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisRadius
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * 회원 등록 — **신규/재등록을 위에서 갈아 끼우며** 회원 정보와 등록권을 넣는다.
 *
 * HiFIS 등록 화면을 그대로 옮겼다 (2026-09-08 대표 지시). 칸과 순서는 같고
 * 생김새만 TeamFIS 것이다.
 *
 * 등록권(회차·결제액)은 **두 모드가 같이 쓴다** — 위쪽만 갈리고 아래는 그대로다.
 *
 * @param referrer 소개한 회원. **셸이 들고 있다** — 고르는 화면이 이 위에 또
 *   얹히므로, 여기서 들면 그 사이에 날아간다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberRegisterScreen(
    onBack: () -> Unit,
    referrer: Member?,
    onPickReferrer: () -> Unit,
    onClearReferrer: () -> Unit,
) {
    var renew by rememberSaveable { mutableStateOf(false) }

    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var visitPath by rememberSaveable { mutableStateOf<VisitPath?>(null) }

    var search by rememberSaveable { mutableStateOf("") }
    var selectedName by rememberSaveable { mutableStateOf<String?>(null) }

    var rounds by rememberSaveable { mutableStateOf("") }
    var payment by rememberSaveable { mutableStateOf("") }

    var existing by rememberSaveable { mutableStateOf(false) }
    var purchasedAt by rememberSaveable { mutableStateOf<Long?>(null) }
    var used by rememberSaveable { mutableStateOf("") }

    var missing by remember { mutableStateOf<String?>(null) }
    var datePickerOpen by remember { mutableStateOf(false) }

    val roundCount = rounds.trim().toIntOrNull() ?: 0
    val paymentWon = payment.trim().toIntOrNull() ?: 0
    val usedCount = if (existing) used.trim().toIntOrNull() ?: 0 else 0
    // 회당 단가 — 결제액 ÷ 회차
    val unitPrice = if (roundCount > 0 && paymentWon > 0) {
        Math.round(paymentWon.toDouble() / roundCount).toInt()
    } else {
        0
    }

    /**
     * 아직 안 채운 것 중 **맨 앞의 하나**. 다 채웠으면 null.
     *
     * 뭉뚱그려 "정보를 입력해주세요" 하면 넷 중 무엇이 빈지 알 수 없다.
     */
    val missingNow: String? = when {
        renew && selectedName == null -> "재등록할 회원을 골라주세요"
        !renew && name.isBlank() -> "성함을 입력해주세요"
        !renew && phone.isBlank() -> "연락처를 입력해주세요"
        !renew && visitPath == null -> "방문 경로를 골라주세요"
        roundCount <= 0 -> "회차를 입력해주세요"
        paymentWon <= 0 -> "결제액을 입력해주세요"
        existing && purchasedAt == null -> "실제 결제일을 골라주세요"
        existing && usedCount > roundCount -> "이미 받은 회차가 총 회차보다 많아요"
        else -> null
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        DetailHeader(title = "회원 등록", onBack = onBack)

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = TeamFisSpacing.screenHorizontal),
        ) {
            SegmentedTabs(
                labels = listOf("신규 회원", "재등록"),
                selected = if (renew) 1 else 0,
                onSelect = { renew = it == 1 },
                modifier = Modifier.padding(top = TeamFisSpacing.sm),
            )

            Spacer(Modifier.height(TeamFisSpacing.xxl))

            if (renew) {
                FieldLabel("재등록할 회원")
                FormField(search, { search = it }, hint = "회원 이름 검색")
                Spacer(Modifier.height(TeamFisSpacing.sm))
                RenewMemberList(
                    query = search,
                    selectedKey = selectedName,
                    onSelect = { selectedName = it },
                )
            } else {
                FieldLabel("회원 정보")
                FormField(name, { name = it }, hint = "성함")
                Spacer(Modifier.height(TeamFisSpacing.sm))
                FormField(
                    phone,
                    { phone = it },
                    hint = "연락처 (010-0000-0000)",
                    keyboardType = KeyboardType.Phone,
                )
                Spacer(Modifier.height(TeamFisSpacing.sm))
                // 소개한 회원은 손으로 적는 이름이 아니라 **등록된 회원을 고른다**
                PickerField(
                    label = "소개한 회원 (선택)",
                    value = referrer?.let { "${it.name} 회원님" },
                    onTap = onPickReferrer,
                    onClear = onClearReferrer,
                )
                if (referrer != null) {
                    ReferrerNote(Modifier.padding(top = TeamFisSpacing.sm))
                }

                Spacer(Modifier.height(TeamFisSpacing.lg))
                FieldLabel("방문 경로")
                ChoiceChips(
                    labels = VisitPath.entries.map { it.label },
                    selected = visitPath?.ordinal,
                    onSelect = { visitPath = VisitPath.entries[it] },
                )
            }

            Spacer(Modifier.height(TeamFisSpacing.xxl))

            FieldLabel("등록권")
            FormField(
                rounds,
                { rounds = it },
                hint = "회차 (예: 30)",
                keyboardType = KeyboardType.Number,
            )
            Spacer(Modifier.height(TeamFisSpacing.sm))
            FormField(
                payment,
                { payment = it },
                hint = "결제액 (원)",
                keyboardType = KeyboardType.Number,
            )

            UnitPriceRow(unitPrice, Modifier.padding(top = TeamFisSpacing.md))

            Spacer(Modifier.height(TeamFisSpacing.xl))

            ToggleRow(
                title = "예전에 등록한 회원",
                description = "앱을 켜기 전에 등록한 건을 뒤늦게 넣을 때",
                checked = existing,
                onCheckedChange = {
                    existing = it
                    // 끄면 값을 버린다 — 남겨 두면 다시 켰을 때 남의 날짜가 서 있다
                    if (!it) {
                        purchasedAt = null
                        used = ""
                    }
                },
            )

            if (existing) {
                Spacer(Modifier.height(TeamFisSpacing.sm))
                PickerField(
                    label = "실제 결제일",
                    value = purchasedAt?.let { dateLabel(it) },
                    onTap = { datePickerOpen = true },
                )
                Spacer(Modifier.height(TeamFisSpacing.sm))
                FormField(
                    used,
                    { used = it },
                    hint = "이미 받은 회차 (예: 5)",
                    keyboardType = KeyboardType.Number,
                )
            }

            Spacer(Modifier.height(TeamFisSpacing.xxxl))
        }

        Column(
            Modifier.padding(
                horizontal = TeamFisSpacing.screenHorizontal,
                vertical = TeamFisSpacing.md,
            ),
        ) {
            missing?.let {
                Text(
                    it,
                    style = TeamFisType.bodySm,
                    color = TeamFisColor.Brand,
                    modifier = Modifier.padding(
                        start = TeamFisSpacing.xs,
                        bottom = TeamFisSpacing.sm,
                    ),
                )
            }
            BottomActionButton(
                label = if (renew) "재등록" else "신규 회원 등록",
                filled = missingNow == null,
                // TODO(서버): 등록 API 가 붙으면 실제로 보낸다
                onClick = { missing = missingNow },
            )
        }
    }

    if (datePickerOpen) {
        // 앞날은 못 고른다 — 지난 등록을 넣는 자리다
        val state = rememberDatePickerState(
            initialSelectedDateMillis = purchasedAt,
            selectableDates = PastOnly,
        )
        DatePickerDialog(
            onDismissRequest = { datePickerOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    purchasedAt = state.selectedDateMillis
                    datePickerOpen = false
                }) { Text("고르기", color = TeamFisColor.Brand) }
            },
            dismissButton = {
                TextButton(onClick = { datePickerOpen = false }) {
                    Text("취소", color = TeamFisColor.TextSecondary)
                }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

/** 재등록에서 고를 회원 줄 — 이름으로 걸러진다. */
@Composable
private fun RenewMemberList(
    query: String,
    selectedKey: String?,
    onSelect: (String) -> Unit,
) {
    // TODO(서버): 내 담당 회원을 받아 쓴다. 자리 표시자는 이름이 다 `000` 이라 검색이 안 갈린다
    val shown = remember(query) {
        placeholderMembers.withIndex().filter { (_, m) ->
            query.isBlank() || m.name.contains(query)
        }
    }

    if (shown.isEmpty()) {
        Text(
            "검색 결과가 없어요",
            style = TeamFisType.bodySm,
            color = TeamFisColor.TextTertiary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = TeamFisSpacing.xxl),
            textAlign = TextAlign.Center,
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(TeamFisSpacing.sm)) {
        shown.forEach { (index, member) ->
            val key = index.toString()
            val isSelected = selectedKey == key
            val interaction = remember(key) { MutableInteractionSource() }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(TeamFisRadius.card)
                    .background(if (isSelected) TeamFisColor.Surface2 else TeamFisColor.Surface1)
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        onClick = { onSelect(key) },
                    )
                    .padding(horizontal = TeamFisSpacing.lg, vertical = TeamFisSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${member.name} 회원님",
                    style = TeamFisType.bodySm,
                    color = TeamFisColor.TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    member.progress,
                    style = TeamFisType.caption.copy(fontFeatureSettings = "tnum"),
                    color = TeamFisColor.TextTertiary,
                )
                // 고른 줄에만 체크. 면 색만으로는 어두운 화면에서 잘 안 보인다
                if (isSelected) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = "선택됨",
                        tint = TeamFisColor.Brand,
                        modifier = Modifier
                            .padding(start = TeamFisSpacing.sm)
                            .size(16.dp),
                    )
                }
            }
        }
    }
}

/** 소개 요율 안내 — 비워 두면 트레이너 몫이 줄어서 눈에 걸려야 한다. */
@Composable
private fun ReferrerNote(modifier: Modifier = Modifier) {
    Text(
        "소개로 온 회원이라 인센티브가 재등록과 같은 요율로 잡혀요",
        style = TeamFisType.caption,
        color = TeamFisColor.Brand,
        modifier = modifier.padding(start = TeamFisSpacing.xs),
    )
}

/** 회당 단가 — 회차·결제액에서 저절로 나온다. 손으로 못 고친다. */
@Composable
private fun UnitPriceRow(unitPrice: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TeamFisSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("회당 단가", style = TeamFisType.bodySm, color = TeamFisColor.TextSecondary)
        Spacer(Modifier.weight(1f))
        Text(
            if (unitPrice > 0) "${comma(unitPrice)}원" else "—",
            style = TeamFisType.bodySm.copy(fontFeatureSettings = "tnum"),
            color = if (unitPrice > 0) TeamFisColor.Brand else TeamFisColor.TextTertiary,
        )
    }
}

/** `1,234,567` */
private fun comma(value: Int): String =
    value.toString().reversed().chunked(3).joinToString(",").reversed()

/** `2026. 3. 14` */
private fun dateLabel(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
    return "${date.year}. ${date.monthValue}. ${date.dayOfMonth}"
}

/** 오늘까지만 고를 수 있다. */
@OptIn(ExperimentalMaterial3Api::class)
private val PastOnly = object : androidx.compose.material3.SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val today = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        return utcTimeMillis <= today
    }
}
