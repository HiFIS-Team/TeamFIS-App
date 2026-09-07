import SwiftUI
import SharedKit

struct ContentView: View {
    var body: some View {
        ZStack {
            // 배경은 검정 (안드로이드와 같다)
            Color.black.ignoresSafeArea()

            VStack(spacing: 0) {
                AppHeader()

                Spacer()
                Text(Greeting().greet())
                    .foregroundStyle(.white)
                Spacer()
            }
        }
    }
}

#Preview {
    ContentView()
}
