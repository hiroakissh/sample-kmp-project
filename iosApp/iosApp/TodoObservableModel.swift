import Foundation
import Shared

@MainActor
final class TodoObservableModel: ObservableObject {
    @Published var state: TodoUiState?

    private let viewModel: TodoViewModel
    private var observation: ObservationHandle?

    init(viewModel: TodoViewModel) {
        self.viewModel = viewModel
        observation = viewModel.observeUiState { [weak self] state in
            DispatchQueue.main.async {
                self?.state = state
            }
        }
    }

    func addTodo(title: String) {
        viewModel.addTodo(title: title)
    }

    func setTodoDone(id: Int64, isDone: Bool) {
        viewModel.setTodoDone(id: id, isDone: isDone)
    }

    func deleteTodo(id: Int64) {
        viewModel.deleteTodo(id: id)
    }

    func deleteDoneTodos() {
        viewModel.deleteDoneTodos()
    }

    deinit {
        observation?.cancel()
    }
}
