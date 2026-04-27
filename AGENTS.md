プロジェクト概要

このリポジトリは、Kotlin Multiplatform を使った TODO サンプルアプリです。

このプロジェクトの目的は、以下の構成を検証・実演することです。

* Kotlin Multiplatform による共通ロジックの共有
* shared モジュールでの Room KMP データベース利用
* shared モジュールでの Repository 実装
* shared モジュールでの AndroidX ViewModel 利用
* Android は Jetpack Compose で UI を実装
* iOS は SwiftUI で UI を実装
* iOS は SwiftUI から shared の Kotlin ViewModel を利用する
* Swift / Kotlin 間の連携改善には SKIE を使う

このプロジェクトは Compose Multiplatform UI のプロジェクトではありません。
明示的に依頼されない限り、iOS UI を Compose Multiplatform に移行しないでください。

⸻

現在のモジュール構成

sample-kmp-project
├── shared
│   ├── src
│   └── schemas
├── apps
│   └── android
├── iosApp
├── gradle
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties

⸻

基本アーキテクチャ方針

このプロジェクトでは、以下の流れを基本とします。

SwiftUI / Jetpack Compose
        ↓
shared KMP ViewModel
        ↓
Repository
        ↓
Room KMP DAO
        ↓
Room KMP Database

アプリケーションの状態とビジネスルールは、基本的に shared モジュールが持ちます。

各プラットフォームアプリの責務は以下です。

* UIの描画
* ユーザー入力の受け取り
* shared ViewModel へのイベント伝達
* プラットフォーム固有のライフサイクル処理
* プラットフォーム固有の初期化処理

⸻

各モジュールの責務

shared

Kotlin Multiplatform の共有モジュールです。

責務:

* Room KMP データベース
* DAO
* Repository
* shared ViewModel
* 共有状態モデル
* UseCase
* Mapper
* platform-specific database builder
* Swiftに公開するためのシンプルなAPI設計
* SKIEでSwiftから扱いやすい Flow / StateFlow / suspend fun の提供

ここに Android UI や SwiftUI などのプラットフォームUIコードを置かないでください。

apps/android

Android アプリケーションです。

責務:

* Jetpack Compose UI
* Android アプリのエントリーポイント
* Android固有のUI配線
* shared KMP ViewModel の取得
* shared ViewModel の状態を画面に描画すること

Android側で、すでに shared に存在するビジネスロジックを重複実装しないでください。

iosApp

iOS アプリケーションです。

責務:

* SwiftUI UI
* iOS アプリのエントリーポイント
* SwiftUI ライフサイクルとの連携
* IosViewModelStoreOwner
* shared Kotlin ViewModel を SwiftUI から利用するための橋渡し
* SKIEによって公開された Swift-friendly API の利用
* Kotlin Flow / StateFlow を Swift AsyncSequence として購読すること
* Kotlin suspend fun を Swift async/await として扱うこと

iOS側で、TODOのビジネスロジックをSwiftで再実装しないでください。

⸻

重要な技術的前提

このプロジェクトでは、AndroidX ViewModel を KMP shared code から利用する構成を検証しています。

AndroidX ViewModel は 2.8.0 以降で Kotlin Multiplatform に対応しています。

iOS の SwiftUI には Android の ViewModelStoreOwner に相当する仕組みが標準では存在しないため、このプロジェクトでは IosViewModelStoreOwner のような iOS 側の所有者を使って、shared ViewModel のライフサイクルを管理します。

この構成は、このプロジェクトの検証対象です。
明確な理由なしに削除・置換しないでください。

⸻

SKIE採用方針

このプロジェクトでは、Swift / Kotlin 間の連携改善のために SKIEを使う方針 とします。

SKIEは、Kotlin Multiplatform が生成する iOS 向け Framework の Swift API を改善するためのツールです。

このプロジェクトでSKIEを使う主な目的は以下です。

* Kotlin Flow / StateFlow を Swift の AsyncSequence として扱いやすくする
* Kotlin suspend fun を Swift の async/await として呼び出しやすくする
* Kotlin sealed class / sealed interface を Swift 側で扱いやすくする
* Kotlin enum を Swift 側で扱いやすくする
* iOS側のコードから Kotlin / Objective-C interop の違和感を減らす
* SwiftUI と shared ViewModel の接続を自然にする

このプロジェクトでは、Swift interop 改善の第一候補を SKIE とします。

⸻

SKIE導入ルール

SKIEを導入・更新する場合は、以下の方針に従ってください。

1. gradle/libs.versions.toml に SKIE のバージョンを定義する
2. shared module に SKIE Gradle plugin を追加する
3. iOS framework generation が壊れていないことを確認する
4. Swift側の呼び出しを SKIE 前提の形に整理する
5. README に SKIE を使っている理由を書く
6. 関係ないリファクタリングと混ぜない

SKIE導入は、以下のような変更と混ぜないでください。

* Room schema変更
* ViewModel設計変更
* Compose UI変更
* SwiftUI画面全面改修
* DI導入
* 大規模なパッケージ構成変更

SKIE関連の変更は、できるだけ独立したPRにしてください。

⸻

SKIEとKMP-NativeCoroutinesの方針

このプロジェクトでは、原則として SKIEを優先 します。

KMP-NativeCoroutines は、明確に必要な理由がある場合のみ検討してください。

SKIEを優先するケース

* Swift Concurrency を中心に使う
* async/await で Kotlin の suspend fun を呼びたい
* Flow / StateFlow を AsyncSequence として扱いたい
* SwiftUI の Task で shared state を購読したい
* iOS側の追加依存を増やしたくない
* SwiftらしいAPIに寄せたい

KMP-NativeCoroutinesを検討してよいケース

* 既存iOSアプリが Combine に強く依存している
* RxSwift連携が必要
* すでにKMP-NativeCoroutinesを前提にした大きな実装がある
* SKIEでは解決できない明確なinterop課題がある

禁止

明確な理由なしに、SKIE と KMP-NativeCoroutines を併用しないでください。

⸻

Swiftに公開するKotlin API方針

Swiftから扱うAPIは、できるだけシンプルにしてください。

良い例:

class TodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {
    val uiState: StateFlow<TodoUiState> = TODO()
    fun addTodo(title: String)
    fun toggleTodo(id: Long)
    fun deleteTodo(id: Long)
    suspend fun refresh()
}

避けるべき例:

Map<String, List<Pair<TodoEntity, Result<Foo>>>>

Swiftに公開する型では、複雑なジェネリクスやネストした型を避けてください。

必要であれば、Swiftから扱いやすい専用モデルを作ってください。

data class TodoUiState(
    val todos: List<TodoItemUiModel>,
    val isLoading: Boolean,
    val errorMessage: String?
)
data class TodoItemUiModel(
    val id: Long,
    val title: String,
    val isCompleted: Boolean
)

⸻

Flow / StateFlow 方針

画面状態は StateFlow を優先します。

val uiState: StateFlow<TodoUiState>

iOS側では、SKIEによって Swift の AsyncSequence として扱うことを前提にします。

Swift側のイメージ:

Task {
    for await state in viewModel.uiState {
        self.uiState = state
    }
}

Flow公開のルール

* 画面状態: StateFlow
* 一度きりの非同期処理: suspend fun
* UIイベント: 必要な場合のみ Flow
* 何でも Flow にしない
* Swift側で購読解除しやすい設計にする
* SwiftUI View のライフサイクルと購読ライフサイクルを一致させる

⸻

suspend fun 方針

一度きりの処理は suspend fun を使ってください。

良い例:

suspend fun refresh()
suspend fun syncTodos()
suspend fun deleteCompletedTodos()

Swift側では、SKIEによって async/await で呼び出せる前提にします。

Swift側のイメージ:

Task {
    do {
        try await viewModel.refresh()
    } catch {
        self.errorMessage = error.localizedDescription
    }
}

ただし、ユーザー操作に対する単純な状態更新は、必ずしも suspend fun にする必要はありません。

fun addTodo(title: String)
fun toggleTodo(id: Long)

DBアクセスやネットワークアクセスを伴う場合は、内部で coroutine scope を使うか、呼び出し側を suspend fun にするかを設計してください。

⸻

sealed class / sealed interface 方針

エラーや状態を表現する場合、Kotlin側では sealed class / sealed interface を使って構いません。

sealed interface TodoError {
    data object Network : TodoError
    data object Database : TodoError
    data class Unknown(val message: String) : TodoError
}

SKIEによってSwift側の取り回しが改善される前提です。

ただし、Swift側で頻繁に扱う必要がある場合は、過度に複雑なsealed hierarchyにしないでください。

必要なら、SwiftUI表示用にはシンプルな String? や TodoErrorUiModel に変換してください。

⸻

ViewModel方針

shared ViewModel は、このプロジェクトの中心的な検証対象です。

以下を守ってください。

* shared ViewModel は shared に置く
* shared ViewModel は AndroidX ViewModel を利用する
* shared ViewModel は shared Repository に依存してよい
* shared ViewModel は Android UI / SwiftUI に直接依存してはいけない
* shared ViewModel は各プラットフォームから扱いやすい状態を公開する
* 画面状態は StateFlow を基本にする
* 単発処理は suspend fun または明示的なイベント関数にする
* lifecycle cleanup は AndroidX ViewModel の挙動と整合させる
* SwiftUI側では IosViewModelStoreOwner などでライフサイクル管理する

明示的な依頼がない限り、shared ViewModel を Android / iOS それぞれの個別 ViewModel に置き換えないでください。

ViewModel が大きくなった場合は、以下に分割してください。

* UseCase
* Repository
* State reducer
* Mapper
* Validator

複雑さを UI 層へ押し出さないでください。

⸻

iOS / SwiftUI 実装方針

iOS UI は SwiftUI を使います。

以下を守ってください。

* SwiftUI View は薄く保つ
* 状態と操作は shared KMP ViewModel を利用する
* TODO Repository やビジネスロジックを Swift で再実装しない
* Swift側で Repository を持たない
* Swift側で DB 操作をしない
* IosViewModelStoreOwner または同等のライフサイクル管理を維持する
* ViewModel は SwiftUI screen 単位で適切にスコープする
* deinit などで ViewModelStore を適切に clear する
* SwiftUI用のScreenModelは @Observable を優先する
* SKIEによって公開された AsyncSequence を使って state を購読する
* SKIE経由の StateFlow 購読結果を ScreenModel の var uiState に代入する
* ユーザー操作は ScreenModel で処理せず、Kotlin ViewModel へ委譲する
* Task のキャンセルを考慮する
* TODO状態の source of truth を複数作らない
* Kotlin ViewModel と別の状態管理を Swift側に作らない
* uiState を Swift側で勝手に加工しすぎない
* Viewファイルを巨大化させず、Screen / Row / Form / EmptyState / ErrorState などに切り分ける
* Previewできるように、実KMP ViewModelや実DBに依存しない表示専用Viewを用意する

Swift側に小さな adapter / ScreenModel 型を置くのは問題ありません。
ただし、domain logic や repository logic をSwift側に複製しないでください。

SwiftUI側の状態管理イメージ

@MainActor
@Observable
final class TodoScreenModel {
    private(set) var uiState: TodoUiState?
    private let viewModel: TodoViewModel
    private var observeTask: Task<Void, Never>?
    init(viewModel: TodoViewModel) {
        self.viewModel = viewModel
        observe()
    }
    private func observe() {
        observeTask = Task {
            // SKIEによって StateFlow を Swift AsyncSequence として購読する想定
            for await state in viewModel.uiState {
                self.uiState = state
            }
        }
    }
    func addTodo(title: String) {
        viewModel.addTodo(title: title)
    }
    deinit {
        observeTask?.cancel()
    }
}

このコードはあくまでイメージです。
実際のAPI名やSKIEによる生成APIに合わせて調整してください。

⸻

iOS Observation Framework 方針

iOS側では、SwiftUIの状態管理に Observation Framework / `@Observable` を使ってよい。
ただし、Observation Framework は iOS UI 層のための仕組みとして使う。
KMP shared ViewModel、Repository、UseCase、Room DB の責務を Swift 側へ移さないこと。

基本構成は以下とする。

```text
shared Kotlin ViewModel
  ↓ StateFlow / Flow / suspend fun
SKIE
  ↓ AsyncSequence / async-await
Swift @Observable ScreenModel
  ↓
SwiftUI View
```

方針

* 新規iOS実装では @Observable を優先する
* iOS 16以下対応が必要な場合のみ ObservableObject / @Published を検討する
* SwiftUI View は @Observable な ScreenModel を参照する
* ScreenModel は shared ViewModel の薄いAdapterにとどめる
* ScreenModel は Repository / DAO / UseCase を直接持たない
* ScreenModel は SKIE 経由で StateFlow を購読する
* ScreenModel は購読用 Task を保持し、deinit で cancel する
* Kotlin側の suspend fun は Swift側で async/await として呼び出す
* TODO状態の source of truth は shared ViewModel に置く

推奨例

@MainActor
@Observable
final class TodoScreenModel {
    private let viewModel: TodoViewModel
    private var observeTask: Task<Void, Never>?
    var uiState: TodoUiState

    init(viewModel: TodoViewModel, initialState: TodoUiState) {
        self.viewModel = viewModel
        self.uiState = initialState
        observe()
    }

    private func observe() {
        observeTask = Task { [weak self] in
            guard let self else { return }
            for await state in viewModel.uiState {
                self.uiState = state
            }
        }
    }

    func addTodo(title: String) {
        viewModel.addTodo(title: title)
    }

    func toggleTodo(id: Int64) {
        viewModel.toggleTodo(id: id)
    }

    func deleteTodo(id: Int64) {
        viewModel.deleteTodo(id: id)
    }

    func refresh() {
        Task {
            do {
                try await viewModel.refresh()
            } catch {
                self.uiState = self.uiState.copy(
                    errorMessage: error.localizedDescription
                )
            }
        }
    }

    deinit {
        observeTask?.cancel()
    }
}

このコードはあくまでイメージです。
実際のAPI名、初期状態生成、`copy` の可否、SKIEによる生成APIに合わせて調整してください。

禁止

* Swift側でRepositoryやDB処理を再実装しない
* shared ViewModelと別のsource of truthを作らない
* @Observable ScreenModelにビジネスロジックを肥大化させない
* SKIEを使っているのに、手動で不要なFlow wrapperを増やさない
* @Observable を KMP ViewModel の置き換えとして扱わない

判断基準

このプロジェクトは iOS 17+ を許容するサンプル / 検証プロジェクトなので、iOS側は Observation Framework 採用を基本方針とする。
ただし、`@Observable` は KMP ViewModel を置き換えるものではなく、SwiftUIに状態を届けるための薄い橋渡しとして使う。

⸻

ネイティブView分割 / Preview方針

Android / iOS ともに、ネイティブViewファイルをおろそかにしないでください。
shared ViewModel にロジックを寄せる一方で、各プラットフォームのViewは読みやすく、Previewしやすい構成にします。

共通方針

* Screen全体、入力フォーム、一覧行、空状態、エラー表示、ローディング表示を適切に切り分ける
* 1つのView / Composableに画面全体の責務を詰め込みすぎない
* Container / Route と Stateless View を分ける
* Container / Route は ViewModel接続、状態購読、イベント委譲だけを担当する
* Stateless View は状態引数とコールバックだけで描画できるようにする
* Previewは Stateless View を対象にする
* Previewから実DB、実Repository、実KMP ViewModelを起動しない
* Preview用のサンプル状態を用意する
* Preview用サンプル状態にビジネスロジックを持たせない
* UIの見た目確認と shared logic の検証を混ぜない

iOS / SwiftUI

* `ContentView` に全UIを詰め込まず、`TodoScreen`、`TodoInputView`、`TodoRowView`、`TodoEmptyView` などへ分割する
* `TodoScreenModel` は `@Observable` な薄いAdapterにとどめる
* Previewは `TodoScreen` などの表示専用Viewにサンプル `TodoUiState` を渡して作る
* Previewのために Swift側 Repository や fake DB を作らない
* Previewが必要な場合は、表示用の fixture / sample state をSwift側に置いてよい
* fixture / sample state はUI確認用に限定し、source of truth として扱わない

Android / Compose

* `MainActivity` に全UIを詰め込まず、`TodoRoute`、`TodoScreen`、`TodoInputRow`、`TodoRow`、`TodoEmptyState` などへ分割する
* `TodoRoute` は ViewModel 取得と `collectAsStateWithLifecycle` に集中させる
* `TodoScreen` 以下は状態引数とイベントコールバックだけで描画できるようにする
* `@Preview` は `TodoScreen` など実DB不要のComposableに付ける
* Preview用のサンプル `TodoUiState` / `TodoItem` を用意してよい
* Previewから `AndroidTodoGraph`、Room、Repository、KMP ViewModelを呼ばない

⸻

Android / Compose 実装方針

Android UI は Jetpack Compose を使います。

以下を守ってください。

* Composable は描画に集中させる
* 状態の source of truth は shared ViewModel にする
* 可能であれば lifecycle-aware に state を collect する
* Repository / Database logic を Android 側で重複実装しない
* Android固有処理は apps/android に閉じ込める
* Preview は UI用のサンプル状態にとどめ、実DBアクセスを行わない
* Viewファイルを巨大化させず、Route / Screen / Component / Preview に分ける

Android側では、通常の Kotlin / Compose の作法で StateFlow を購読して構いません。

⸻

Room KMP 方針

Room database logic は shared に置きます。

以下を守ってください。

* DAO は可能な限り shared code に置く
* Database builder の差分は platform-specific code で吸収する
* schema 変更時は migration を考慮する
* 既存の schema file を安易に削除しない
* Android専用のRoom APIを common code から使わない

Room KMP にはプラットフォーム差があります。
Android専用APIが common / iOS から使えないことがあるため、API選定には注意してください。

⸻

Repository / UseCase 方針

Repository は shared に置きます。

ViewModel
  ↓
UseCase
  ↓
Repository
  ↓
DAO

小規模な機能では UseCase を省略しても構いません。

ただし、ViewModel が肥大化し始めたら UseCase を切り出してください。

Repository の責務:

* DAO / local data source の呼び出し
* 必要に応じた data mapping
* 永続化処理
* 将来的な remote data source 追加に備えた抽象化

Repository に UI 表示都合の文言を入れないでください。

⸻

Kotlin実装ルール

以下を守ってください。

* ビジネスロジックは shared に置く
* UIロジックは各プラットフォーム側に置く
* UI状態はできるだけ immutable にする
* 画面状態の監視には StateFlow を優先する
* 単発処理には suspend fun を優先する
* Swiftから扱いづらい複雑なジェネリクス型を公開しない
* Android専用APIを commonMain に漏らさない
* プラットフォーム固有処理は expect/actual を使う
* Room Entity / DAO の変更時は migration を考慮する
* ビジネスロジック変更時はテスト追加・更新を検討する

良い shared API の例:

val uiState: StateFlow<TodoUiState>
fun addTodo(title: String)
fun toggleTodo(id: Long)
fun deleteTodo(id: Long)
suspend fun refresh()

Swift側に公開しないほうがよいAPIの例:

Map<String, List<Pair<TodoEntity, Result<Foo>>>>

Swiftから扱いやすいように、必要なら専用のシンプルなモデルを作ってください。

⸻

バージョン方針

README に記載されている現在の前提バージョンを尊重してください。

明確な互換性問題がない限り、以下のバージョンを理由なく下げないでください。

* Kotlin
* Android Gradle Plugin
* Gradle
* AndroidX Lifecycle / ViewModel
* Room KMP
* SQLite
* Compose BOM
* SKIE

バージョンを変更する場合は、以下も合わせて更新してください。

* gradle/libs.versions.toml
* README
* 影響を受ける Gradle 設定
* 必要に応じて iOS 側のビルド設定

⸻

ビルド確認コマンド

Androidアプリのビルド確認は、基本的に以下を使ってください。

./gradlew :apps:android:assembleDebug

shared モジュールの確認は、以下を優先してください。

./gradlew :shared:build

iOS framework generation の確認が必要な場合は、Xcode build または該当する Gradle task を確認してください。

iOS は以下を Xcode で開きます。

iosApp/iosApp.xcodeproj

Xcode の Run Script phase では、以下が実行される想定です。

:shared:embedAndSignAppleFrameworkForXcode

同等の framework integration に置き換える明確な理由がない限り、このスクリプトを削除しないでください。

⸻

依存ライブラリ追加方針

依存ライブラリは、明確に複雑さを減らす場合のみ追加してください。

追加前に確認すること:

* Kotlin Multiplatform 対応か
* iOS target をサポートしているか
* 現在の Kotlin version と互換性があるか
* Swift interop を悪化させないか
* TODO sample app に本当に必要か
* SKIEと競合しないか

原則として、勝手に追加しないもの:

* 大きなDIフレームワーク
* API同期が未実装なのに networking library
* Compose Multiplatform UI
* 不要な Swift package dependency
* KMP-NativeCoroutines

⸻

テスト方針

shared のビジネスロジックを変更した場合は、可能な限り shared test を追加・更新してください。

優先的にテストするもの:

* Repository の挙動
* ViewModel の状態遷移
* DAO の操作
* Database migration
* TODO の作成
* TODO の更新
* TODO の完了
* TODO の削除

ビジネスルールのテストは、Android / iOS に重複して書くよりも、shared の Kotlin test に寄せてください。

⸻

Migration 方針

Room schema を変更する場合は、以下の順で対応してください。

1. Entity を更新する
2. 必要なら DAO を更新する
3. migration が必要か判断する
4. 必要なら migration を追加する
5. schema file を更新する
6. Android build を確認する
7. iOS framework generation を確認する
8. 挙動が変わる場合は README を更新する

破壊的な schema 変更を行う場合は、必ずその理由を明記してください。

⸻

Git / PR 方針

変更は小さく、目的を絞ってください。

良いPR例:

* Add SKIE for Swift interop
* Use SKIE AsyncSequence from SwiftUI
* Expose TodoUiState from shared ViewModel
* Fix iOS ViewModel lifecycle cleanup
* Add completedAt to TodoEntity
* Add Room migration for todo completion timestamp

悪いPR例:

* Refactor everything
* Migrate iOS to Compose
* Add DI, networking, auth, and new UI
* Rewrite architecture
* Add SKIE and redesign ViewModel and change DB schema

⸻

Agent の作業手順

タスクを受けたら、以下の順で進めてください。

1. README を読む
2. 関連するモジュールを確認する
    * shared logic → shared
    * Android UI → apps/android
    * iOS UI → iosApp
3. 現在の設計を把握してから変更する
4. SKIE前提のSwift interopを壊さないようにする
5. 最小限かつ安全な変更にする
6. 可能であれば関連するビルドコマンドを実行する
7. 最後に以下を報告する
    * 何を変更したか
    * なぜ変更したか
    * SKIEとの関係
    * 変更しなかったこと
    * どう確認したか

⸻

禁止事項

以下は行わないでください。

* iOS UI を勝手に Compose Multiplatform へ移行する
* SwiftUI を削除する
* shared ViewModel を明示的な依頼なしに platform-only ViewModel へ置き換える
* shared Repository logic を Android / iOS に重複実装する
* Swift側に Repository を持たせる
* Swift側から DB 操作を行う
* TODO のビジネスルールを Swift側で再実装する
* Kotlin ViewModel と別の TODO 状態管理を Swift側に作る
* shared の uiState を Swift側で過度に加工して別の source of truth にする
* SKIEを外す
* 明確な理由なしに KMP-NativeCoroutines を追加する
* 明確な理由なしに Koin / Ktor / SQLDelight / Compose Multiplatform を追加する
* アーキテクチャ変更と機能追加を混ぜる
* expect/actual なしに platform-specific behavior を common code へ隠す
* iOS framework generation を壊す
* Room schema を安易に削除する
* Kotlin / AGP / Gradle / Room / Lifecycle / SKIE のバージョンを理由なく下げる

⸻

このプロジェクトの望ましい方向性

このプロジェクトは、以下の問いに集中してください。

Android と iOS のネイティブUIを維持しながら、ViewModel を含む KMP shared logic をどこまで現実的に共有できるか？
さらに、SKIEによってSwiftUIからどこまで自然にshared codeを扱えるか？

そのため、基本方針は以下です。

KMP shared:
- state
- ViewModel
- repository
- database
- business rules
- Flow / StateFlow
- suspend function
- Swift-friendly API boundary
SKIE:
- Flow / StateFlow の Swift AsyncSequence 化
- suspend fun の Swift async/await 連携
- sealed class / enum の Swift 利用性改善
Android:
- Compose UI
- lifecycle wiring
iOS:
- SwiftUI UI
- lifecycle wiring
- SKIE経由でshared ViewModelを利用

明示的な依頼がない限り、汎用的なフルスタックサンプルへ拡張しないでください。

⸻

今後の改善候補

以下は将来的な改善候補です。
依頼されない限り、自動で実装しないでください。

1. SKIE導入状態のREADME整理
2. SwiftUI側の AsyncSequence 購読コード整理
3. shared ViewModel のテスト追加
4. Room migration sample の追加
5. TODO 操作時の error state 追加
6. createdAt / updatedAt / completedAt の追加
7. all / active / completed のフィルタ追加
8. constructor が複雑になった場合のみ軽量DIを検討
9. SwiftUI用の小さな adapter / screen model の整理
10. SKIEで扱いやすい sealed error model の追加

⸻

判断基準

迷った場合は、以下の優先順位で判断してください。

1. shared にビジネスロジックを集約できているか
2. Android / iOS でロジックが重複していないか
3. SwiftUI側がSKIEによって自然に書けているか
4. Kotlin APIがSwiftから扱いやすい形になっているか
5. ViewModelのライフサイクルが壊れていないか
6. Room schema / migration が安全か
7. 変更が小さく、原因切り分けしやすいか

このプロジェクトでは、単に「KMPで共有できる」だけでは不十分です。
SwiftUIから見ても自然に使えるshared APIになっているか を重視してください。
