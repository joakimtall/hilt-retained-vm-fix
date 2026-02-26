package com.example.hiltretained.core.retained

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.currentCompositeKeyHashCode
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified T : RetainedComponent> hiltRetained(
    key: String? = null,
    noinline factory: () -> T,
): T {
    val compositeKey = currentCompositeKeyHashCode
    val finalKey = remember(key) {
        key ?: "${T::class.java.name}:$compositeKey"
    }
    val store: RetainedStoreViewModel = viewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val component = remember(finalKey) { store.getOrCreate(finalKey, factory) }
    DisposableEffect(finalKey) {
        onDispose {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                store.remove(finalKey)
            }
        }
    }
    return component
}
