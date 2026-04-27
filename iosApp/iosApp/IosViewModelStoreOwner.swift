import Shared
import SwiftUI

final class IosViewModelStoreOwner: ObservableObject, ViewModelStoreOwner {
    let viewModelStore = ViewModelStore()

    func viewModel<T: ViewModel>(
        key: String? = nil,
        factory: ViewModelProviderFactory,
        extras: CreationExtras? = nil
    ) -> T {
        do {
            return try viewModelStore.resolveViewModel(
                modelClass: T.self,
                factory: factory,
                key: key,
                extras: extras
            ) as! T
        } catch {
            fatalError("Failed to create ViewModel of type \(T.self): \(error)")
        }
    }

    deinit {
        viewModelStore.clear()
    }
}
