import SwiftUI

/// 회원 등록 — **신규/재등록을 위에서 갈아 끼우며** 회원 정보와 등록권을 넣는다.
///
/// HiFIS 등록 화면을 그대로 옮겼다 (2026-09-08 대표 지시). 칸과 순서는 같고
/// 생김새만 TeamFIS 것이다.
///
/// 등록권(회차·결제액)은 **두 모드가 같이 쓴다** — 위쪽만 갈리고 아래는 그대로다.
struct MemberRegisterScreen: View {
    let onBack: () -> Void
    /// 소개한 회원. **뿌리가 들고 있다** — 고르는 잎이 이 위에 또 얹히기 때문이다
    let referrer: Member?
    let onPickReferrer: () -> Void
    let onClearReferrer: () -> Void

    @State private var renew = false

    @State private var name = ""
    @State private var phone = ""
    @State private var visitPath: VisitPath?

    @State private var search = ""
    @State private var selectedKey: Int?

    @State private var rounds = ""
    @State private var payment = ""

    @State private var existing = false
    @State private var purchasedAt: Date?
    @State private var used = ""

    @State private var missing: String?
    @State private var datePickerOpen = false

    private var roundCount: Int { Int(rounds.trimmingCharacters(in: .whitespaces)) ?? 0 }
    private var paymentWon: Int { Int(payment.trimmingCharacters(in: .whitespaces)) ?? 0 }
    private var usedCount: Int {
        existing ? (Int(used.trimmingCharacters(in: .whitespaces)) ?? 0) : 0
    }

    /// 회당 단가 — 결제액 ÷ 회차
    private var unitPrice: Int {
        guard roundCount > 0, paymentWon > 0 else { return 0 }
        return Int((Double(paymentWon) / Double(roundCount)).rounded())
    }

    /// 아직 안 채운 것 중 **맨 앞의 하나**. 다 채웠으면 nil.
    ///
    /// 뭉뚱그려 "정보를 입력해주세요" 하면 넷 중 무엇이 빈지 알 수 없다.
    private var missingNow: String? {
        if renew {
            if selectedKey == nil { return "재등록할 회원을 골라주세요" }
        } else {
            if name.trimmingCharacters(in: .whitespaces).isEmpty { return "성함을 입력해주세요" }
            if phone.trimmingCharacters(in: .whitespaces).isEmpty { return "연락처를 입력해주세요" }
            if visitPath == nil { return "방문 경로를 골라주세요" }
        }
        if roundCount <= 0 { return "회차를 입력해주세요" }
        if paymentWon <= 0 { return "결제액을 입력해주세요" }
        if existing {
            if purchasedAt == nil { return "실제 결제일을 골라주세요" }
            if usedCount > roundCount { return "이미 받은 회차가 총 회차보다 많아요" }
        }
        return nil
    }

    var body: some View {
        VStack(spacing: 0) {
            DetailHeader(title: "회원 등록", onBack: onBack)

            // **스크롤 밖에 둔다.** 이 값이 아래 칸들의 뜻을 정하는데,
            // 같이 밀려 올라가면 등록권을 적는 동안 어느 모드인지 안 보인다
            SegmentedTabs(
                labels: ["신규 회원", "재등록"],
                selected: renew ? 1 : 0,
                onSelect: { renew = $0 == 1 }
            )
            .padding(.horizontal, TeamFisSpacing.screenHorizontal)
            .padding(.vertical, TeamFisSpacing.sm)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    Spacer().frame(height: TeamFisSpacing.md)

                    existingSection

                    Spacer().frame(height: TeamFisSpacing.xxl)

                    if renew {
                        renewSection
                    } else {
                        newSection
                    }

                    Spacer().frame(height: TeamFisSpacing.xxl)

                    ticketSection
                }
                .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                .padding(.bottom, TeamFisSpacing.xxxl)
            }

            VStack(alignment: .leading, spacing: TeamFisSpacing.sm) {
                if let missing {
                    Text(missing)
                        .font(TeamFisFont.bodySm)
                        .foregroundStyle(TeamFisColor.brand)
                        .padding(.leading, TeamFisSpacing.xs)
                }
                // TODO(서버): 등록 API 가 붙으면 실제로 보낸다
                BottomActionButton(
                    label: renew ? "재등록" : "신규 회원 등록",
                    filled: missingNow == nil,
                    action: { missing = missingNow }
                )
            }
            .padding(.horizontal, TeamFisSpacing.screenHorizontal)
            .padding(.vertical, TeamFisSpacing.md)
        }
        .sheet(isPresented: $datePickerOpen) { datePicker }
    }

    // MARK: - 신규

    private var newSection: some View {
        VStack(alignment: .leading, spacing: 0) {
            FieldLabel("회원 정보")
            FormField(text: $name, hint: "성함")
            Spacer().frame(height: TeamFisSpacing.sm)
            FormField(text: $phone, hint: "연락처 (010-0000-0000)", keyboard: .phonePad)
            Spacer().frame(height: TeamFisSpacing.sm)
            // 소개한 회원은 손으로 적는 이름이 아니라 **등록된 회원을 고른다**
            PickerField(
                label: "소개한 회원 (선택)",
                value: referrer.map { "\($0.name) 회원님" },
                onTap: onPickReferrer,
                onClear: onClearReferrer
            )
            if referrer != nil {
                // 비워 두면 트레이너 몫이 줄어서 눈에 걸려야 한다
                Text("소개로 온 회원이라 인센티브가 재등록과 같은 요율로 잡혀요")
                    .font(TeamFisFont.caption)
                    .foregroundStyle(TeamFisColor.brand)
                    .padding(.top, TeamFisSpacing.sm)
                    .padding(.leading, TeamFisSpacing.xs)
            }

            Spacer().frame(height: TeamFisSpacing.lg)
            FieldLabel("방문 경로")
            ChoiceChips(
                labels: VisitPath.allCases.map(\.label),
                selected: visitPath.flatMap { VisitPath.allCases.firstIndex(of: $0) },
                onSelect: { visitPath = VisitPath.allCases[$0] }
            )
        }
    }

    // MARK: - 재등록

    private var renewSection: some View {
        VStack(alignment: .leading, spacing: 0) {
            FieldLabel("재등록할 회원")
            FormField(text: $search, hint: "회원 이름 검색")
            Spacer().frame(height: TeamFisSpacing.sm)

            // TODO(서버): 내 담당 회원을 받아 쓴다. 자리 표시자는 이름이 다 `000` 이라 검색이 안 갈린다
            let shown = Array(Member.placeholder.enumerated()).filter { _, member in
                search.trimmingCharacters(in: .whitespaces).isEmpty || member.name.contains(search)
            }

            if shown.isEmpty {
                Text("검색 결과가 없어요")
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textTertiary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, TeamFisSpacing.xxl)
            } else {
                VStack(spacing: TeamFisSpacing.sm) {
                    ForEach(shown, id: \.offset) { index, member in
                        renewRow(index: index, member: member)
                    }
                }
            }
        }
    }

    private func renewRow(index: Int, member: Member) -> some View {
        let isSelected = selectedKey == index
        return Button(action: { selectedKey = index }) {
            HStack(spacing: TeamFisSpacing.sm) {
                Text("\(member.name) 회원님")
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textPrimary)
                Spacer(minLength: 0)
                Text(member.progress)
                    .font(TeamFisFont.caption.monospacedDigit())
                    .foregroundStyle(TeamFisColor.textTertiary)
                // 고른 줄에만 체크. 면 색만으로는 어두운 화면에서 잘 안 보인다
                if isSelected {
                    Image("ic_check")
                        .renderingMode(.template)
                        .resizable()
                        .frame(width: 16, height: 16)
                        .foregroundStyle(TeamFisColor.brand)
                }
            }
            .padding(.horizontal, TeamFisSpacing.lg)
            .padding(.vertical, TeamFisSpacing.md)
            .background(
                RoundedRectangle(cornerRadius: TeamFisRadius.card, style: .continuous)
                    .fill(isSelected ? TeamFisColor.surface2 : TeamFisColor.surface1)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    // MARK: - 어떤 등록인지

    /// **어떤 등록인지부터 정한다** (2026-09-08 대표 지시). 켜면 아래 칸들이
    /// 지난 실적으로 잡히므로, 다 적고 나서 묻는 것보다 먼저 묻는 게 맞다.
    /// 딸린 칸(실제 결제일·이미 받은 회차)도 같이 올렸다 — 토글만 올리면
    /// 켰을 때 어디에 칸이 생겼는지 못 찾는다
    private var existingSection: some View {
        VStack(alignment: .leading, spacing: 0) {
            ToggleRow(
                title: "예전에 등록한 회원",
                description: "앱을 켜기 전에 등록한 건을 뒤늦게 넣을 때",
                isOn: Binding(
                    get: { existing },
                    set: { on in
                        existing = on
                        // 끄면 값을 버린다 — 남겨 두면 다시 켰을 때 남의 날짜가 서 있다
                        if !on {
                            purchasedAt = nil
                            used = ""
                        }
                    }
                )
            )

            if existing {
                Spacer().frame(height: TeamFisSpacing.sm)
                PickerField(
                    label: "실제 결제일",
                    value: purchasedAt.map(dateLabel),
                    onTap: { datePickerOpen = true }
                )
                Spacer().frame(height: TeamFisSpacing.sm)
                FormField(text: $used, hint: "이미 받은 회차 (예: 5)", keyboard: .numberPad)
            }
        }
    }

    // MARK: - 등록권 (두 모드 공통)

    private var ticketSection: some View {
        VStack(alignment: .leading, spacing: 0) {
            FieldLabel("등록권")
            FormField(text: $rounds, hint: "회차 (예: 30)", keyboard: .numberPad)
            Spacer().frame(height: TeamFisSpacing.sm)
            FormField(text: $payment, hint: "결제액 (원)", keyboard: .numberPad)

            // 회당 단가 — 회차·결제액에서 저절로 나온다. 손으로 못 고친다
            HStack(spacing: TeamFisSpacing.md) {
                Text("회당 단가")
                    .font(TeamFisFont.bodySm)
                    .foregroundStyle(TeamFisColor.textSecondary)
                Spacer(minLength: 0)
                Text(unitPrice > 0 ? "\(comma(unitPrice))원" : "—")
                    .font(TeamFisFont.bodySm.monospacedDigit())
                    .foregroundStyle(unitPrice > 0 ? TeamFisColor.brand : TeamFisColor.textTertiary)
            }
            .padding(.top, TeamFisSpacing.md)
            .padding(.horizontal, TeamFisSpacing.xs)

        }
    }

    // MARK: - 날짜 고르개

    private var datePicker: some View {
        // 앞날은 못 고른다 — 지난 등록을 넣는 자리다
        DatePicker(
            "실제 결제일",
            selection: Binding(get: { purchasedAt ?? Date() }, set: { purchasedAt = $0 }),
            in: ...Date(),
            displayedComponents: .date
        )
        .datePickerStyle(.graphical)
        .tint(TeamFisColor.brand)
        .padding()
        .presentationDetents([.medium])
    }

    /// `2026. 3. 14`
    private func dateLabel(_ date: Date) -> String {
        let parts = Calendar.current.dateComponents([.year, .month, .day], from: date)
        return "\(parts.year ?? 0). \(parts.month ?? 0). \(parts.day ?? 0)"
    }

    /// `1,234,567`
    private func comma(_ value: Int) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        return formatter.string(from: NSNumber(value: value)) ?? "\(value)"
    }
}
