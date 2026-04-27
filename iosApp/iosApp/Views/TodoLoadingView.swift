import SwiftUI

struct TodoLoadingView: View {
    var message: String?

    init(message: String? = nil) {
        self.message = message
    }

    var body: some View {
        if let message {
            ProgressView(message)
        } else {
            ProgressView()
        }
    }
}

#Preview("Default") {
    TodoLoadingView()
}

#Preview("Message") {
    TodoLoadingView(message: "Loading TODOs")
}
