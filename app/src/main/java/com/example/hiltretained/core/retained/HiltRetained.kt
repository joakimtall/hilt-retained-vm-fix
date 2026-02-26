package com.example.hiltretained.core.retained

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified T : RetainedComponent> hiltRetained(
    key: String,
    noinline factory: () -> T,
): T {
    val store: RetainedStoreViewModel = viewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val component = remember(key) { store.getOrCreate(key, factory) }
    DisposableEffect(key) {
        onDispose {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                store.remove(key)
            }
        }
    }
    return component
}
