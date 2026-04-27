import SwiftUI

@MainActor
final class TodoModelHolder: ObservableObject {
    @Published var model: TodoObservableModel?
}
