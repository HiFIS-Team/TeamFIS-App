import SwiftUI

/// 마이 — 헤더 오른쪽 끝의 사람 아이콘을 누르면 들어온다.
///
/// **트레이너 자신에 대한 자리다.** 나머지 네 탭이 전부 회원과 수업이라 `나` 를
/// 볼 데가 없었다.
///
/// 순서는 **나 → 번 것 → 한 것 → 설정**이다. 이번 달 실적이 위에 오는 것은
/// 트레이너가 이 화면을 여는 이유가 대개 그 숫자여서다.
///
/// **내 몫만 브랜드 색이다.** 매출은 가게 것이고 그 밑이 내 것이라, 사이에 선을
/// 하나 긋고 무게를 달리 준다.
///
/// 홈과 겹칠 수 있다 — 홈은 **오늘 할 일**, 여기는 **나에 대한 것**으로 가른다.
///
/// **잎 화면이다** — 뿌리(`AppRoot`)가 오른쪽에서 밀어 넣어 하단 유리 바까지 덮는다.
struct MyScreen: View {
    let onBack: () -> Void

    private let profile = TrainerProfile.placeholder

    var body: some View {
        VStack(spacing: 0) {
            DetailHeader(title: "마이", onBack: onBack)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    TrainerHeader(profile: profile)
                        .padding(.top, TeamFisSpacing.sm)

                    Rectangle()
                        .fill(TeamFisColor.divider)
                        .frame(height: 1)
                        .padding(.vertical, TeamFisSpacing.xl)

                    MySectionHeader("이번 달")
                    MyStatCard {
                        MyStatRow(label: "신규", value: "\(profile.newMembers)건")
                        MyStatRow(label: "재등록", value: "\(profile.renewals)건")
                        MyStatRow(label: "매출", value: "\(profile.revenue.commaString)원")
                        MyStatDivider()
                        // TODO(서버): 요율은 지점이 정한다 — 금액을 받아서 그대로 적는다
                        MyStatRow(
                            label: "내 인센티브",
                            value: "\(profile.incentive.commaString)원",
                            highlight: true
                        )
                    }

                    MySectionHeader("수업")
                        .padding(.top, TeamFisSpacing.xxxl)
                    MyStatCard {
                        MyStatRow(label: "진행", value: "\(profile.doneRounds)회차")
                        MyStatRow(label: "노쇼", value: "\(profile.noShowRounds)회차")
                    }

                    MySectionHeader("설정")
                        .padding(.top, TeamFisSpacing.xxxl)
                    VStack(spacing: TeamFisSpacing.sm) {
                        // TODO: 알림 설정 · 로그아웃이 붙으면 연결한다
                        MySettingRow(label: "알림") {}
                        MySettingRow(label: "로그아웃", danger: true) {}
                    }

                    MyVersionNote(version: "버전 0.1.0")
                        .padding(.top, TeamFisSpacing.xl)
                }
                .padding(.horizontal, TeamFisSpacing.screenHorizontal)
                .padding(.bottom, TeamFisSpacing.xxxl)
            }
        }
    }
}

extension TrainerProfile {
    /// 데이터가 붙기 전까지 쓰는 **자리 표시자**다. 서버가 내 실적을 주면 통째로 걷어낸다.
    /// 이름은 다른 화면과 같이 아직 `000` 이다.
    static let placeholder = TrainerProfile(
        name: "000",
        branch: "000점",
        phone: "010-0000-0000",
        newMembers: 3,
        renewals: 5,
        revenue: 12_400_000,
        incentive: 2_480_000,
        doneRounds: 48,
        noShowRounds: 2
    )
}
