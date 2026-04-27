import Shared
import SwiftUI

struct TodoRowView: View {
    let todo: TodoItem
    let onToggle: () -> Void
    let onDelete: () -> Void

    var body: some View {
        HStack {
            Button {
                onToggle()
            } label: {
                Image(systemName: todo.isDone ? "checkmark.circle.fill" : "circle")
            }
            .buttonStyle(.plain)

            Text(todo.title)
                .strikethrough(todo.isDone)

            Spacer()

            Button(role: .destructive) {
                onDelete()
            } label: {
                Image(systemName: "trash")
            }
            .buttonStyle(.plain)
        }
    }
}

#Preview("Active") {
    List {
        TodoRowView(
            todo: .previewActive,
            onToggle: {},
            onDelete: {}
        )
    }
}

#Preview("Done") {
    List {
        TodoRowView(
            todo: .previewDone,
            onToggle: {},
            onDelete: {}
        )
    }
}
