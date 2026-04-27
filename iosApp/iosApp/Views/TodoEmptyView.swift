import SwiftUI

struct TodoEmptyView: View {
    var body: some View {
        Text("No TODOs yet")
            .foregroundStyle(.secondary)
    }
}

#Preview {
    List {
        TodoEmptyView()
    }
}
