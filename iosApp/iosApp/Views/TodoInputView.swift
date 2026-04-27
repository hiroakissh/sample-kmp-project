import Foundation
import SwiftUI

struct TodoInputView: View {
    @Binding var title: String
    let onAdd: () -> Void

    private var canAdd: Bool {
        !title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    var body: some View {
        HStack {
            TextField("New TODO", text: $title)
                .textInputAutocapitalization(.sentences)

            Button("Add") {
                onAdd()
            }
            .disabled(!canAdd)
        }
    }
}

private struct TodoInputPreview: View {
    @State private var title = "Buy milk"

    var body: some View {
        List {
            TodoInputView(title: $title, onAdd: {})
        }
    }
}

#Preview {
    TodoInputPreview()
}
