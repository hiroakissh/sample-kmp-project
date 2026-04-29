# Lifecycle Design

このドキュメントは、shared の `TodoViewModel` を Android / iOS のネイティブUIから使うときに、ライフサイクル差分をどう扱うかを整理します。

このプロジェクトでは、TODO の source of truth は shared の `TodoViewModel` です。Android / iOS 側は、ViewModel の取得、状態購読、ユーザー操作の委譲、プラットフォーム固有ライフサイクルへの接続だけを担当します。

## 基本構成

```text
Android Compose / iOS SwiftUI
        ↓
platform lifecycle owner
        ↓
shared Kotlin TodoViewModel
        ↓
viewModelScope
        ↓
Repository
        ↓
Room KMP Database
```

重要なのは、Android と iOS で「画面の再生成」と「ViewModel の生存期間」の意味が違うことです。

## Android

Android 側は、AndroidX の標準的な `ViewModelStoreOwner` と Compose lifecycle に寄せています。

現在の接続点:

- `apps/android/src/main/java/com/example/kmptodo/android/ui/TodoRoute.kt`
- `viewModel(factory = AndroidTodoGraph.viewModelFactory(context))`
- `collectAsStateWithLifecycle()`

Android で担保されること:

- `TodoViewModel` は `LocalViewModelStoreOwner` にスコープされます。
- このアプリは Navigation をまだ使っていないため、実質的には `MainActivity` の `ViewModelStore` にスコープされます。
- Activity の configuration change では、通常 `ViewModelStore` が維持され、`TodoViewModel` も維持されます。
- `collectAsStateWithLifecycle()` により、Compose は Lifecycle が有効な間だけ `StateFlow` を collect します。
- `TodoViewModel` の `viewModelScope` は `ViewModel` が clear されたときにキャンセルされます。

Android 側の責務:

- `TodoRoute` は ViewModel 取得と StateFlow 購読だけを担当します。
- `TodoScreen` 以下は stateless UI として、状態と callback を受け取って描画します。
- Repository / DB / business rule は Android 側に持ちません。
- Preview は `TodoPreviewFixtures.kt` の表示用 state を使い、実DBや実ViewModelを起動しません。

## iOS

iOS / SwiftUI には、Android の `ViewModelStoreOwner` と同じ仕組みは標準で存在しません。SwiftUI の `View` は value type で、`body` の再評価やView再構成が頻繁に起きるため、Kotlin `ViewModel` を `View` の素朴な property として持つとライフサイクルが不安定になります。

このプロジェクトでは、iOS 側に明示的な所有者を置いて補っています。

現在の接続点:

- `iosApp/iosApp/IosViewModelStoreOwner.swift`
- `iosApp/iosApp/TodoObservableModel.swift`
- `iosApp/iosApp/TodoModelHolder.swift`
- `iosApp/iosApp/Views/ContentView.swift`

iOS で担保していること:

- `ContentView` が `@StateObject` で `IosViewModelStoreOwner` を保持します。
- `IosViewModelStoreOwner` が shared の `ViewModelStore` を持ちます。
- SwiftUI の `body` が再評価されても、`@StateObject` により同じ owner が維持されます。
- `ContentView` の `deinit` に到達すると、`IosViewModelStoreOwner.deinit` で `viewModelStore.clear()` が呼ばれます。
- `TodoObservableModel` は shared `TodoViewModel` を薄く包む adapter です。
- `TodoObservableModel.deinit` で `ObservationHandle.cancel()` を呼び、StateFlow 購読を止めます。

iOS 側の責務:

- `IosViewModelStoreOwner` は Kotlin `ViewModel` のライフサイクル所有者です。
- `TodoObservableModel` は SwiftUI へ状態を届ける薄い adapter です。
- SwiftUI View は `TodoObservableModel` にイベントを渡し、そこから Kotlin `TodoViewModel` へ委譲します。
- Repository / DB / TODO business rule は iOS 側に持ちません。
- Preview は fixture state を使い、実DBや実ViewModelを起動しません。

## Shared ViewModel

shared の `TodoViewModel` は `androidx.lifecycle.ViewModel` です。

現在の接続点:

- `shared/src/commonMain/kotlin/com/example/kmptodo/shared/TodoViewModel.kt`

shared 側で担保されること:

- `TodoViewModel` は `StateFlow<TodoUiState>` を公開します。
- DB購読と更新処理は `viewModelScope` で実行します。
- `ViewModel` が clear されると、`viewModelScope` の coroutine はキャンセルされます。
- UI状態は `TodoUiState` に集約されます。

## Android と iOS の差分

| 観点 | Android | iOS |
| --- | --- | --- |
| ViewModel owner | `ViewModelStoreOwner` が標準で存在 | 標準では存在しないため自前で用意 |
| UI再構成 | Compose recomposition | SwiftUI `body` 再評価 / View再生成 |
| 状態購読 | `collectAsStateWithLifecycle()` | 現状は `observeUiState()` + `ObservationHandle` |
| clear契機 | owner の `ViewModelStore` clear | `IosViewModelStoreOwner.deinit` で明示的に clear |
| coroutine cleanup | `viewModelScope` が自動キャンセル | Kotlin ViewModel が clear されれば同じ |
| Preview | stateless Composable に fixture を渡す | stateless SwiftUI View に fixture を渡す |

## 現時点の注意点

この構成で一番難しいのは、iOS 側のライフサイクルが Android ほど自動ではない点です。

特に注意すること:

- iOS では `IosViewModelStoreOwner` を短命な SwiftUI `View` の通常 property にしない。
- iOS では `@StateObject` などの reference lifetime を維持できる仕組みで owner / adapter を保持する。
- `viewModelStore.clear()` を呼べない構成にしない。
- StateFlow 購読を開始したら、`deinit` や `Task.cancel()` で止められる構成にする。
- Swift側に別の source of truth を作らない。
- Swift側で Repository / DAO / DB を持たない。
- Preview用fixtureを本番状態管理として扱わない。

## 保証していないこと

このプロジェクトのライフサイクル設計は、shared ViewModel をネイティブUIから安全に扱うための最小構成です。すべてのOS状態復元を自動で担保するものではありません。

現時点で保証していないこと:

- Android の process death 後に、入力中テキストや一時UI状態を完全復元すること。
- iOS の process kill / memory pressure 後に、SwiftUI adapter の状態を復元すること。
- iOS の複数window / 複数sceneで、どの単位に shared ViewModel をスコープするか。
- Navigation 導入後に、画面ごとの ViewModel lifetime が自動的に揃うこと。
- iOS の `deinit` がアプリ終了時に必ず実行されること。

永続化されるべき TODO データは Room DB に置きます。一方、入力中テキスト、スクロール位置、選択中フィルタなどの transient UI state は、別途 platform ごとの復元方針を決める必要があります。

## SKIE導入後の方針

現時点の iOS 実装は、Kotlin 側の `observeUiState()` と `ObservationHandle` による手動ブリッジです。

SKIE 導入後は、StateFlow を Swift の AsyncSequence として扱う方針に寄せます。ただし、ライフサイクル上の考え方は変えません。

SKIE導入後の理想形:

```text
shared TodoViewModel
  ↓ StateFlow<TodoUiState>
SKIE
  ↓ AsyncSequence
Swift @Observable ScreenModel
  ↓ var uiState
SwiftUI View
```

SKIE導入後も守ること:

- Kotlin `TodoViewModel` は shared に置く。
- Swift の `@Observable ScreenModel` は薄い adapter にする。
- `Task` を保持し、`deinit` で cancel する。
- `uiState` は shared ViewModel から届いた状態を反映するだけにする。
- Swift側で business rule を再実装しない。

## 追加開発時の判断基準

画面や機能を追加するときは、まずスコープを決めます。

- アプリ全体で共有する状態か。
- 画面単位で閉じる状態か。
- Navigation destination 単位で破棄したい状態か。
- Android と iOS で同じ lifetime に見せたい状態か。

判断ルール:

- business state は shared `ViewModel` に置く。
- transient UI state は platform UI 側に置いてよい。
- Android は `ViewModelStoreOwner` のスコープを明示する。
- iOS は `IosViewModelStoreOwner` の所有位置を明示する。
- Navigation を導入するときは、screen単位の owner を作るか、親ownerで共有するかを先に決める。
- owner の lifetime が不明なまま ViewModel を生成しない。

## 残っている課題

このプロジェクトはサンプルなので、まだ以下は明示的な検証対象です。

- iOS の `ObservableObject` 実装を `@Observable` + SKIE AsyncSequence に寄せること。
- iOS Navigation 導入時の screen単位 `ViewModelStoreOwner` 設計。
- Android Navigation 導入時の back stack entry scope 設計。
- process death 後の復元方針。
- 複数window / scene での iOS owner scope。
- StateFlow 購読のキャンセル漏れを検知するテストやログ。

このため、KMP shared ViewModel を iOS ネイティブUIへ深く接続する設計は、Androidより明示的なルールが必要です。このドキュメントは、そのルールを曖昧にしないための基準です。
