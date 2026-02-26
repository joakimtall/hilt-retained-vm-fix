# hiltPresenter: Composition-Scoped Injection

A lightweight abstraction for Jetpack Compose that bridges the gap between `remember` and `ViewModel`. It provides Hilt-injected state holders that survive **Rotation** and **Navigation**, but properly clean up when removed from **Conditional Composition**.

## The Problem

**The Issue:** If you use `hiltViewModel()` inside an `if (showChat)` block, the ViewModel **stays alive** even after `showChat` becomes false, leaking memory and state until the user leaves the entire screen.

Standard Compose state solutions have limitations in complex scenarios:

| Solution | Rotation | Navigation (BackStack) | Conditional (`if`) Cleanup |
| :--- | :--- | :--- | :--- |
| **`hiltViewModel()`** | ✅ Survives | ✅ Survives | ❌ **LEAKS** (Lives until screen dies) |
| **`retain`** (New API) | ✅ Survives | ❌ **DIES** (Lost on nav push) | ✅ Cleans up |
| **`remember`** | ❌ Dies | ❌ Dies | ✅ Cleans up |

## The Solution: `hiltPresenter`

`hiltPresenter` is a hybrid approach. It uses a generic `ViewModel` store to persist objects across configuration changes and navigation, but uses a smart `DisposableEffect` to detect when a specific Composable leaves the UI tree while the screen is still active.

| Solution | Rotation | Navigation (BackStack) | Conditional (`if`) Cleanup |
| :--- | :--- | :--- | :--- |
| **`hiltPresenter`** | ✅ **Survives** | ✅ **Survives** | ✅ **Cleans up** |

## Sample App

This repository includes a sample app demonstrating the lifecycle behavior with a **Counter** feature.

<img src="screenshot.png" alt="Sample app screenshot" width="300" />

### How to Test
1.  **Launch App:** You will see a "Show Counter" button.
2.  **Toggle ON:** The Counter appears (Count: 0). Increment it to **5**.
3.  **Rotate Device:** The count remains **5**. (Proves Rotation Survival).
4.  **Navigate:** Click "Go to Details", then press Back. The count remains **5**. (Proves Navigation Survival).
5.  **Toggle OFF:** Click "Hide Counter". The counter disappears.
6.  **Toggle ON:** The counter reappears, reset to **0**. (Proves Conditional Cleanup).

Check Logcat for `CounterPresenter` messages to verify when the coroutine is cancelled and the object is destroyed.

## Usage

### 1. Define your Presenter
Extend `Presenter` and use standard Hilt `@AssistedInject`.

```kotlin
class ChatPresenter @AssistedInject constructor(
    private val repo: ChatRepository,
    @Assisted val chatId: String
) : Presenter() {

    init {
        presenterScope.launch {
            // Coroutine that lives as long as the presenter
        }
    }

    override fun onCleared() {
        super.onCleared() // cancels presenterScope
        println("Chat $chatId destroyed")
    }

    @AssistedFactory
    interface Factory { fun create(id: String): ChatPresenter }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Entry { fun factory(): Factory }
}
```

### 2. Use in Compose
Call `hiltPresenter` inside your conditional logic.

```kotlin
if (isChatOpen) {
    // Automatically scoped to this block.
    // Dies if isChatOpen becomes false.
    // Survives if you rotate or navigate to a new screen.
    val presenter = hiltPresenter { entry: ChatPresenter.Entry ->
        entry.factory().create("chat_123")
    }

    ChatUI(presenter)
}
```

## Installation

Copy the `core/presenter/` package into your project:
1.  `Presenter.kt` (Base class with coroutine scope)
2.  `PresenterStore.kt` (The ViewModel-backed store)
3.  `HiltPresenter.kt` (The composable abstraction)
