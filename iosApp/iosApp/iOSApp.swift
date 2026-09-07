import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                // 배경이 검정이라 라이트 모드로 뜨지 않게 고정한다
                .preferredColorScheme(.dark)
        }
    }
}
