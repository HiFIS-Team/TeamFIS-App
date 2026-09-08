package com.teamfis.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.teamfis.app.ui.components.FormField
import com.teamfis.app.ui.components.Member
import com.teamfis.app.ui.components.MemberDivider
import com.teamfis.app.ui.components.MemberRow
import com.teamfis.app.ui.components.placeholderMembers
import com.teamfis.app.ui.shell.DetailHeader
import com.teamfis.app.ui.theme.TeamFisColor
import com.teamfis.app.ui.theme.TeamFisSpacing
import com.teamfis.app.ui.theme.TeamFisType

/**
 * 소개한 회원 고르기 — 등록 화면 **위에 한 겹 더 얹히는 잎**이다.
 *
 * 서버가 이름이 아니라 회원 id 를 받으므로 손으로 적게 두지 않는다.
 * 고르면 바로 닫히고 등록 화면으로 값이 돌아간다.
 */
@Composable
fun ReferrerPickScreen(onBack: () -> Unit, onPick: (Member) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val shown = remember(query) {
        if (query.isBlank()) placeholderMembers else placeholderMembers.filter { it.name.contains(query) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(TeamFisColor.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        DetailHeader(title = "소개한 회원", onBack = onBack)

        Text(
            "이 회원을 데려온 기존 회원을 골라주세요",
            style = TeamFisType.caption,
            color = TeamFisColor.TextTertiary,
            modifier = Modifier.padding(
                horizontal = TeamFisSpacing.screenHorizontal,
                vertical = TeamFisSpacing.sm,
            ),
        )

        FormField(
            query,
            { query = it },
            hint = "회원 이름 검색",
            modifier = Modifier.padding(
                horizontal = TeamFisSpacing.screenHorizontal,
                vertical = TeamFisSpacing.sm,
            ),
        )

        if (shown.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("검색 결과가 없어요", style = TeamFisType.bodySm, color = TeamFisColor.TextMuted)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = TeamFisSpacing.xxxl)) {
                itemsIndexed(shown) { index, member ->
                    if (index > 0) MemberDivider()
                    MemberRow(member, onClick = { onPick(member) })
                }
            }
        }
    }
}
