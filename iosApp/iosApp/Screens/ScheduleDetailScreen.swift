import SwiftUI

/// 수업 상세 — 일정에서 수업 카드를 눌렀을 때.
///
/// **머리에 날짜가 선다.** 회원 상세 머리가 `000 회원님` 이라 여기도 이름이면 두 화면이
/// 같은 머리를 이고 있게 된다. 이 화면의 정체는 **어느 날 몇 시 수업**이고, 이름은
/// 본문 첫 줄에서 제일 큰 글자로 받는다.
///
/// 순서는 **누구 → 무엇 → 언제 → 처리**다. 날짜·시간만 칸에 들어 있는데,
/// **칸이 곧 고칠 수 있다는 표시**다. 나머지는 줄로만 적어 못 고치는 것과 갈라 둔다.
///
/// 회원 상세의 회차 카드는 그대로 두었다 — 지금 수업 하나에 이르는 길은 일정뿐이다.
///
/// **잎 화면이다** — 뿌리(`AppRoot`)가 오른쪽에서 밀어 넣어 하단 유리 바까지 덮는다.
/// 그래서 바 몫의 아래 여백(`bottomBarClearance`)이 필요 없다.
struct ScheduleDetailScreen: View {
    let item: ScheduleClass
    let onBack: () -> Void

    /// 고친 날짜·시간은 여기까지만이다. 자리 표시자라 뒤로 갔다 오면 원래대로 —
    /// TODO(서버): 일정 수정 API 가 붙으면 고칠 때마다 보낸다
    @State private var at: Date
    @State private var datePickerOpen = false
    @State private var timePickerOpen = false

    init(item: ScheduleClass, onBack: @escaping () -> Void) {
        self.item = item
        self.onBack = onBack
        _at = State(initialValue: item.at)
    }

    private var log: ScheduleLog? { Self.placeholderLog(for: item) }

    var body: some View {
        // 회원 상세와 같은 `VStack { 고정; ScrollView }` 모양을 지킨다
        VStack(spacing: 0) {
            DetailHeader(title: dayTitle(at), onBack: onBack)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    HStack(spacing: TeamFisSpacing.md) {
                        Text("\(item.member) 회원님")
                            .font(TeamFisFont.titleLg)
                            .foregroundStyle(TeamFisColor.textPrimary)
                        Spacer(minLength: 0)
                        SessionBadge(status: item.status)
                    }
                    .padding(.top, TeamFisSpacing.sm)
                    .padding(.horizontal, TeamFisSpacing.screenHorizontal)

                    VStack(spacing: TeamFisSpacing.md) {
                        DetailInfoRow(label: "등록권", value: item.product)
                        DetailInfoRow(label: "회차", value: item.progress)
                    }
                    .padding(.top, TeamFisSpacing.xl)
                    .padding(.horizontal, TeamFisSpacing.screenHorizontal)

                    Rectangle()
                        .fill(TeamFisColor.divider)
                        .frame(height: 1)
                        .padding(.top, TeamFisSpacing.xl)
                        .padding(.horizontal, TeamFisSpacing.screenHorizontal)

                    VStack(alignment: .leading, spacing: 0) {
                        FieldLabel("날짜")
                        PickerField(label: "날짜", value: dayLabel(at)) { datePickerOpen = true }

                        FieldLabel("시간")
                            .padding(.top, TeamFisSpacing.lg)
                        PickerField(label: "시간", value: timeRange(at, item.minutes)) {
                            timePickerOpen = true
                        }
                    }
                    .padding(.top, TeamFisSpacing.lg)
                    .padding(.horizontal, TeamFisSpacing.screenHorizontal)

                    // **두 카드는 상태와 상관없이 늘 선다** (2026-09-08 대표 지시).
                    // 채워질 자리를 미리 보여 주는 것이라 아직 없으면 값만 비운다
                    VStack(spacing: TeamFisSpacing.md) {
                        ScheduleLogBlock(log: log)
                        // 사인은 수업을 끝내야 받는다 — 예정에도 노쇼에도 없다
                        ScheduleSignBlock(signed: item.status == .done)
                    }
                    .padding(.top, TeamFisSpacing.xl)
                    .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                }
                .padding(.bottom, TeamFisSpacing.xxxl)
            }

            // **처리는 화면 아래에 붙인다** (2026-09-08 대표 지시). 흐르는 값 사이에 끼면
            // 스크롤 위치에 따라 있다 없다 하고, 아래가 통째로 비어 보인다.
            // 등록 화면의 `BottomActionButton` 과 같은 자리다
            if item.status == .scheduled {
                HStack(spacing: TeamFisSpacing.md) {
                    // TODO(서버): 회차 처리 API 가 붙어야 실제로 바뀐다 (회원 상세와 같다)
                    SessionButton(label: "노쇼", background: TeamFisColor.surface2,
                                  content: TeamFisColor.textSecondary, action: {})
                    SessionButton(label: "완료", background: TeamFisColor.brand,
                                  content: TeamFisColor.textPrimary, action: {})
                }
                .frame(height: 48)
                .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                .padding(.vertical, TeamFisSpacing.md)
            }
        }
        .sheet(isPresented: $datePickerOpen) { datePicker }
        .sheet(isPresented: $timePickerOpen) { timePicker }
    }

    // MARK: - 고르개
    //
    // 등록 화면의 날짜 고르개와 같은 모양이다 — 시트 + `.graphical`.

    private var datePicker: some View {
        // 앞날도 고를 수 있다 — 수업을 미루는 자리라 지난 날만 될 이유가 없다
        DatePicker(
            "날짜",
            selection: Binding(get: { at }, set: { at = onDate(at, $0) }),
            displayedComponents: .date
        )
        .datePickerStyle(.graphical)
        .tint(TeamFisColor.brand)
        .padding()
        .presentationDetents([.medium])
    }

    private var timePicker: some View {
        DatePicker("시간", selection: $at, displayedComponents: .hourAndMinute)
            .datePickerStyle(.wheel)
            .labelsHidden()
            .tint(TeamFisColor.brand)
            .padding()
            .presentationDetents([.height(260)])
    }

    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 일지를 주면 통째로 걷어낸다.
    /// 끝난 수업에만 일지가 있다 — 노쇼는 한 게 없어서 쓸 것도 없다.
    static func placeholderLog(for item: ScheduleClass) -> ScheduleLog? {
        guard item.status == .done else { return nil }
        return ScheduleLog(
            parts: [.back, .arm, .cardio],
            note: "랫풀다운 자세가 많이 좋아졌어요. 다음 시간에는 중량을 올려 봐요."
        )
    }
}
