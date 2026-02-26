package com.example.hiltretained.core.retained

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.currentCompositeKeyHashCode
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Hybrid retained composable: survives navigation & rotation (ViewModel-backed store),
 * but cleans up when removed from conditional composition (if-block toggled off).
 *
 * The key insight is the lifecycle check inside [DisposableEffect.onDispose]:
 * - **RESUMED** — the screen is fully visible, so the composable was removed by an
 *   if-block toggling off (conditional removal). Clean up immediately.
 * - **STARTED** — the [NavBackStackEntry][androidx.navigation.NavBackStackEntry] lifecycle
 *   drops to STARTED before composables leave during navigation. Don't clean up; the
 *   ViewModel keeps it alive so it's restored when the user navigates back.
 * - **CREATED/DESTROYED** — configuration change (rotation). The ViewModel survives, and
 *   composition is rebuilt from scratch on the new Activity.
 */
@Composable
inline fun <reified T : RetainedComponent> hiltModel(
    key: String? = null,
    noinline factory: () -> T,
): T {
    val compositeKey = currentCompositeKeyHashCode
    val finalKey = remember(key) {
        val id = key ?: compositeKey.toString()
        "${T::class.java.name}:$id"
    }
    val store: RetainedStoreViewModel = viewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val component = remember(finalKey) { store.getOrCreate(finalKey, factory) }
    DisposableEffect(finalKey) {
        onDispose {
            // RESUMED means the user is still on this screen and the composable
            // left the tree due to a recomposition (conditional removal). Any other
            // state means navigation or config change — leave the component alive.
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                store.remove(finalKey)
            }
        }
    }
    return component
}
