import SwiftUI

struct TodoRoute: View {
    @ObservedObject var model: TodoObservableModel

    var body: some View {
        TodoScreen(
            state: model.state,
            onAddTodo: model.addTodo(title:),
            onSetTodoDone: model.setTodoDone(id:isDone:),
            onDeleteTodo: model.deleteTodo(id:),
            onDeleteDoneTodos: model.deleteDoneTodos
        )
    }
}
