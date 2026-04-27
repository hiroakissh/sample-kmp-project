import SwiftUI

struct TodoErrorView: View {
    let message: String

    var body: some View {
        Text(message)
            .foregroundStyle(.red)
    }
}

#Preview {
    List {
        TodoErrorView(message: "Failed to load TODOs.")
    }
}
