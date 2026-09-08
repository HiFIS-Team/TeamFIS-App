import Foundation

/// 회원에게 권한 영양제 한 줄.
///
/// 칸 이름은 **트레이너가 쓰던 표 그대로**다 (HiFIS 에서 가져왔다) —
/// 영양제 / 얼마나? / 언제? / 왜? / 기억하기.
struct Supplement: Identifiable, Hashable {
    var id = UUID()
    var name: String
    var dose = ""
    var timing = ""
    var reason = ""
    var note = ""

    /// 목록 한 줄에서 이름 밑에 붙는 말 — 빈 칸은 건너뛴다
    var summary: String {
        [dose, timing].filter { !$0.isEmpty }.joined(separator: " · ")
    }
}

/// 자주 쓰는 영양제 표 — 트레이너가 쓰던 노션 표를 그대로 옮겼다 (HiFIS 와 같은 값).
///
/// **고르면 네 칸이 함께 채워진다.** 매번 손으로 적으면 회원마다 말이 달라지고,
/// 그러면 회원이 트레이너마다 다른 안내를 받는다. 골라 넣고 그 회원에 맞게 고친다.
///
/// **여기 없는 것은 직접 적는다** — 이 목록이 울타리는 아니다.
let supplementPresets: [Supplement] = [
    Supplement(name: "오메가3", dose: "1000~3000mg", timing: "아침식후",
                reason: "성인병 예방, 염증완화, 세포 단위 개선",
                note: "식사 직후"),
    Supplement(name: "마그네슘", dose: "200~400mg", timing: "저녁식후",
                reason: "스트레스, 피로, 근육경련, 성인병 예방",
                note: "저녁식후"),
    Supplement(name: "비타민C", dose: "500~1500 정도 분포", timing: "점심 저녁 사이 (위장장애시 식후)",
                reason: "피로회복, 혈관·두피 건강, 콜라겐 강화",
                note: "낮에 드세요 (수면 방해)"),
    Supplement(name: "비타민D", dose: "1000~4000IU", timing: "점심식후",
                reason: "면역력, 항암, 뼈 건강",
                note: "낮에 식후 (수면 방해)"),
    Supplement(name: "비타민E", dose: "100~600mg", timing: "점심 저녁사이 (위장장애시 식후)",
                reason: "항산화, 노화 예방, 항암",
                note: "속쓰림 빈번하면 식후가 나을 수도"),
    Supplement(name: "종합비타민", dose: "하루 1~2알 (제품마다 상이)", timing: "식사 직후",
                reason: "피로 개선, 영양소 불균형",
                note: "위장장애 없으면 아침, 위장장애 있으면 식후"),
    Supplement(name: "유산균", dose: "1일 1~3회 1캡슐", timing: "아침식전",
                reason: "장염예방, 장건강, 두뇌건강",
                note: "공복, 식후 큰 차이 없음"),
    Supplement(name: "칼슘", dose: "250~1000mg (최대한 나눠서)", timing: "식사 직후",
                reason: "뼈 건강, 골다공증 예방, 수면개선",
                note: "마그네슘과 따로 먹기"),
    Supplement(name: "철분", dose: "15~40mg", timing: "취침전",
                reason: "어지러움, 두통, 소화불량, 수족냉증",
                note: "위장장애시 식후 섭취"),
    Supplement(name: "루테인", dose: "10~20mg", timing: "식후 섭취",
                reason: "눈 건강, 피부 건강, 뇌 건강",
                note: "빌베리와 함께 섭취 시 시너지"),
    Supplement(name: "빌베리추출물", dose: "200~800mg (추출물)", timing: "식사 무관",
                reason: "눈건강, 혈당 조절, 혈관 건강",
                note: "케일, 커큐민과 함께 섭취 시 시너지"),
    Supplement(name: "알파리포산", dose: "200~600mg", timing: "공복 섭취",
                reason: "혈당 조절, 항산화, 식욕조절, 신경재생",
                note: "미네랄과 같이 섭취 금지"),
    Supplement(name: "커큐민", dose: "100~500mg", timing: "식사 후",
                reason: "염증 완화, 항산화, 항암",
                note: "흡수를 높이려면 식사 직후 무관"),
    Supplement(name: "소화효소", dose: "50~100단위 (최대 3회)", timing: "식사 직후",
                reason: "소화불량 개선, 위장건강",
                note: "식후 바로 섭취"),
    Supplement(name: "마늘추출물", dose: "500~1500mg", timing: "아침 또는 점심 식후",
                reason: "고혈압 예방, 고지혈증, 혈액응집, 수족냉증",
                note: "위장장애시 식사 직후 섭취"),
    Supplement(name: "단백질", dose: "15~30g", timing: "식사 무관",
                reason: "근손실 예방, 혈당 부스터",
                note: "식사 무관"),
    Supplement(name: "콜라겐", dose: "1000~6000mg", timing: "공복 섭취",
                reason: "피부 보습, 피부 주름 개선, 손톱 건강, 관절 건강, 혈관 건강",
                note: "위장장애 시 식후, 비타민C와 함께 섭취"),
    Supplement(name: "케일파우더", dose: "케일로서 15~30g 정도", timing: "점심과 저녁사이",
                reason: "장건강, 디톡스, 혈당 조절",
                note: "위장장애시 식후"),
]
