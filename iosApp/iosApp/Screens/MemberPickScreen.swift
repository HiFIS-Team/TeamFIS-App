import SwiftUI

/// 회원 고르기 — 등록 화면 **위에 한 겹 더 얹히는 잎**이다.
///
/// `소개한 회원` 과 `재등록할 회원` 이 같이 쓴다. 하는 일이 똑같아서
/// (담당 회원에서 한 명 골라 돌려주기) **제목과 안내만 갈아 끼운다.**
///
/// 서버가 이름이 아니라 회원 id 를 받으므로 손으로 적게 두지 않는다.
/// 고르면 바로 닫히고 등록 화면으로 값이 돌아간다.
struct MemberPickScreen: View {
    let title: String
    let description: String
    let onBack: () -> Void
    let onPick: (Member) -> Void

    @State private var query = ""

    private var shown: [Member] {
        let trimmed = query.trimmingCharacters(in: .whitespaces)
        guard !trimmed.isEmpty else { return Member.placeholder }
        return Member.placeholder.filter { $0.name.contains(trimmed) }
    }

    var body: some View {
        VStack(spacing: 0) {
            DetailHeader(title: title, onBack: onBack)

            Text(description)
                .font(TeamFisFont.caption)
                .foregroundStyle(TeamFisColor.textTertiary)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                .padding(.vertical, TeamFisSpacing.sm)

            FormField(text: $query, hint: "회원 이름 검색")
                .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                .padding(.vertical, TeamFisSpacing.sm)

            if shown.isEmpty {
                Spacer()
                Text("검색 결과가 없어요")
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textMuted)
                Spacer()
            } else {
                ScrollView {
                    LazyVStack(spacing: 0) {
                        ForEach(Array(shown.enumerated()), id: \.element.id) { index, member in
                            if index > 0 {
                                MemberDivider()
                            }
                            MemberRow(member: member) { onPick(member) }
                        }
                    }
                    .padding(.bottom, TeamFisSpacing.xxxl)
                }
            }
        }
    }
}
