import Shared
import SwiftUI

struct ContentView: View {
    @StateObject private var viewModelStoreOwner = IosViewModelStoreOwner()
    @StateObject private var modelHolder = TodoModelHolder()

    var body: some View {
        Group {
            if let model = modelHolder.model {
                TodoScreen(model: model)
            } else {
                ProgressView("Loading TODOs")
            }
        }
        .onAppear {
            if modelHolder.model == nil {
                let viewModel: TodoViewModel = viewModelStoreOwner.viewModel(
                    factory: IosTodoGraph.shared.viewModelFactory()
                )
                modelHolder.model = TodoObservableModel(viewModel: viewModel)
            }
        }
    }
}

struct TodoScreen: View {
    @ObservedObject var model: TodoObservableModel
    @State private var title = ""

    private var todos: [TodoItem] {
        model.state?.todos as? [TodoItem] ?? []
    }

    var body: some View {
        NavigationStack {
            List {
                Section {
                    HStack {
                        TextField("New TODO", text: $title)
                            .textInputAutocapitalization(.sentences)
                        Button("Add") {
                            model.addTodo(title: title)
                            title = ""
                        }
                    }
                }

                if let errorMessage = model.state?.errorMessage {
                    Section {
                        Text(errorMessage)
                            .foregroundStyle(.red)
                    }
                }

                Section {
                    if model.state?.isLoading == true {
                        ProgressView()
                    } else if todos.isEmpty {
                        Text("No TODOs yet")
                            .foregroundStyle(.secondary)
                    } else {
                        ForEach(todos, id: \.id) { todo in
                            HStack {
                                Button {
                                    model.setTodoDone(id: todo.id, isDone: !todo.isDone)
                                } label: {
                                    Image(systemName: todo.isDone ? "checkmark.circle.fill" : "circle")
                                }
                                .buttonStyle(.plain)

                                Text(todo.title)
                                    .strikethrough(todo.isDone)

                                Spacer()

                                Button(role: .destructive) {
                                    model.deleteTodo(id: todo.id)
                                } label: {
                                    Image(systemName: "trash")
                                }
                                .buttonStyle(.plain)
                            }
                        }
                    }
                }
            }
            .navigationTitle("KMP TODO")
            .toolbar {
                Button("Clear done") {
                    model.deleteDoneTodos()
                }
            }
        }
    }
}
