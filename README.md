# Sample KMP TODO

Kotlin Multiplatform の共有層で Room DB、Repository、AndroidX ViewModel を管理し、Android は Jetpack Compose、iOS は SwiftUI のネイティブUIから利用する TODO アプリです。

## Modules

- `shared`: KMP共有モジュール。Room DB、DAO、Repository、`TodoViewModel` を配置。
- `apps/android`: Androidネイティブアプリ。`AndroidTodoGraph` からKMP ViewModelを取得。
- `iosApp`: iOSネイティブアプリ。`IosViewModelStoreOwner` でKMP ViewModelをSwiftUI画面にスコープ。

## Versions

- Kotlin `2.3.20`
- Android Gradle Plugin `9.1.1`
- Gradle `9.3.1`
- AndroidX Lifecycle/ViewModel `2.10.0`
- Room KMP `2.8.4`
- SQLite Bundled `2.6.2`
- Compose BOM `2026.04.01`
- Android compile/target SDK `36`

## Build

AGP 9.1.x のため JDK 17 以上が必要です。

```bash
./gradlew :apps:android:assembleDebug
```

iOS は `iosApp/iosApp.xcodeproj` を開きます。Xcode の Run Script phase が `:shared:embedAndSignAppleFrameworkForXcode` を実行して `Shared.framework` を生成・連携します。

## Design docs

- [Lifecycle Design](docs/lifecycle.md): Android / iOS のライフサイクル差分と、shared ViewModel のスコープ方針。

## References

- Android KMP ViewModel: https://developer.android.com/kotlin/multiplatform/viewmodel?hl=ja
- Compose Multiplatform Common ViewModel: https://kotlinlang.org/docs/multiplatform/compose-viewmodel.html#using-viewmodel-in-common-code
- Android KMP Room: https://developer.android.com/kotlin/multiplatform/room?hl=ja
