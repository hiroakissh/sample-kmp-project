import Shared

extension TodoItem {
    static var previewActive: TodoItem {
        TodoItem(id: 1, title: "Buy milk", isDone: false)
    }

    static var previewDone: TodoItem {
        TodoItem(id: 2, title: "Read KMP docs", isDone: true)
    }
}

extension TodoUiState {
    static var previewLoaded: TodoUiState {
        TodoUiState(
            todos: [
                .previewActive,
                .previewDone,
                TodoItem(id: 3, title: "Open Xcode preview", isDone: false),
            ],
            isLoading: false,
            errorMessage: nil
        )
    }

    static var previewEmpty: TodoUiState {
        TodoUiState(todos: [], isLoading: false, errorMessage: nil)
    }
}
