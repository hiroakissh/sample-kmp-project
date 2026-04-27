import Shared
import SwiftUI

struct ContentView: View {
    @StateObject private var viewModelStoreOwner = IosViewModelStoreOwner()
    @StateObject private var modelHolder = TodoModelHolder()

    var body: some View {
        Group {
            if let model = modelHolder.model {
                TodoRoute(model: model)
            } else {
                TodoLoadingView(message: "Loading TODOs")
            }
        }
        .onAppear {
            prepareModelIfNeeded()
        }
    }

    private func prepareModelIfNeeded() {
        guard modelHolder.model == nil else { return }

        let viewModel: TodoViewModel = viewModelStoreOwner.viewModel(
            factory: IosTodoGraph.shared.viewModelFactory()
        )
        modelHolder.model = TodoObservableModel(viewModel: viewModel)
    }
}
