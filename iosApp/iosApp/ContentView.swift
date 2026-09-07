import SwiftUI
import SharedKit

struct ContentView: View {
    var body: some View {
        Text(Greeting().greet())
    }
}

#Preview {
    ContentView()
}
