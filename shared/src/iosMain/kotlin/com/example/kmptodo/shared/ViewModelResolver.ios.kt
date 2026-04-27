package com.example.kmptodo.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import kotlin.reflect.KClass

@BetaInteropApi
@Throws(IllegalArgumentException::class)
fun ViewModelStore.resolveViewModel(
    modelClass: ObjCClass,
    factory: ViewModelProvider.Factory,
    key: String?,
    extras: CreationExtras? = null,
): ViewModel {
    @Suppress("UNCHECKED_CAST")
    val viewModelClass = getOriginalKotlinClass(modelClass) as? KClass<ViewModel>
    require(viewModelClass != null) { "The modelClass parameter must be a ViewModel type." }

    val provider = ViewModelProvider.Companion.create(
        this,
        factory,
        extras ?: CreationExtras.Empty,
    )
    return key?.let { provider[it, viewModelClass] } ?: provider[viewModelClass]
}
