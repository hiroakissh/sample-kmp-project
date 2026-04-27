import Shared
import SwiftUI

struct TodoScreen: View {
    let state: TodoUiState?
    let onAddTodo: (String) -> Void
    let onSetTodoDone: (Int64, Bool) -> Void
    let onDeleteTodo: (Int64) -> Void
    let onDeleteDoneTodos: () -> Void

    @State private var title = ""

    private var todos: [TodoItem] {
        state?.todos as? [TodoItem] ?? []
    }

    var body: some View {
        NavigationStack {
            List {
                Section {
                    TodoInputView(title: $title) {
                        onAddTodo(title)
                        title = ""
                    }
                }

                if let errorMessage = state?.errorMessage {
                    Section {
                        TodoErrorView(message: errorMessage)
                    }
                }

                Section {
                    if state?.isLoading != false {
                        TodoLoadingView()
                    } else if todos.isEmpty {
                        TodoEmptyView()
                    } else {
                        ForEach(todos, id: \.id) { todo in
                            TodoRowView(
                                todo: todo,
                                onToggle: {
                                    onSetTodoDone(todo.id, !todo.isDone)
                                },
                                onDelete: {
                                    onDeleteTodo(todo.id)
                                }
                            )
                        }
                    }
                }
            }
            .navigationTitle("KMP TODO")
            .toolbar {
                Button("Clear done") {
                    onDeleteDoneTodos()
                }
                .disabled(!todos.contains { $0.isDone })
            }
        }
    }
}

#Preview("Todos") {
    TodoScreen(
        state: .previewLoaded,
        onAddTodo: { _ in },
        onSetTodoDone: { _, _ in },
        onDeleteTodo: { _ in },
        onDeleteDoneTodos: {}
    )
}

#Preview("Empty") {
    TodoScreen(
        state: .previewEmpty,
        onAddTodo: { _ in },
        onSetTodoDone: { _, _ in },
        onDeleteTodo: { _ in },
        onDeleteDoneTodos: {}
    )
}
