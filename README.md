# Hilt Model: Composition-Scoped Injection

A lightweight abstraction for Jetpack Compose that bridges the gap between `remember` and `ViewModel`. It provides Hilt-injected state holders that survive **Rotation** and **Navigation**, but properly clean up when removed from **Conditional Composition**.

## The Problem

Standard Compose state solutions have limitations in complex scenarios:

| Solution | Rotation | Navigation (BackStack) | Conditional (`if`) Cleanup |
| :--- | :--- | :--- | :--- |
| **`hiltViewModel()`** | ✅ Survives | ✅ Survives | ❌ **LEAKS** (Lives until screen dies) |
| **`retain`** (New API) | ✅ Survives | ❌ **DIES** (Lost on nav push) | ✅ Cleans up |
| **`remember`** | ❌ Dies | ❌ Dies | ✅ Cleans up |

**The Issue:** If you use `hiltViewModel()` inside an `if (showChat)` block, the ViewModel **stays alive** even after `showChat` becomes false, leaking memory and state until the user leaves the entire screen.

## The Solution: `hiltModel`

`hiltModel` is a hybrid approach. It uses a generic `ViewModel` store to persist objects across configuration changes and navigation, but uses a smart `DisposableEffect` to detect when a specific Composable leaves the UI tree while the screen is still active.

| Solution | Rotation | Navigation (BackStack) | Conditional (`if`) Cleanup |
| :--- | :--- | :--- | :--- |
| **`hiltModel`** | ✅ **Survives** | ✅ **Survives** | ✅ **Cleans up** |

## Usage

### 1. Define your Component
Implement `RetainedComponent` and use standard Hilt `@AssistedInject`.

```kotlin
class ChatPresenter @AssistedInject constructor(
    private val repo: ChatRepository,
    @Assisted val chatId: String
) : RetainedComponent {

    override fun onCleared() {
        // Called immediately when 'if' block exits
        // OR when the screen is finally destroyed
        println("Chat $chatId destroyed") 
    }

    @AssistedFactory
    interface Factory { fun create(id: String): ChatPresenter }
    
    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface Entry { fun factory(): Factory }
}
```

### 2. Use in Compose
Call `hiltModel` inside your conditional logic.

```kotlin
if (isChatOpen) {
    // Automatically scoped to this block.
    // Dies if isChatOpen becomes false.
    // Survives if you rotate or navigate to a new screen.
    val presenter = hiltModel { entry: ChatPresenter.Entry ->
        entry.factory().create("chat_123")
    }
    
    ChatUI(presenter)
}
```

## Installation

Copy the `core/retained/` package into your project:
1.  `RetainedComponent.kt` (Interface)
2.  `RetainedStoreViewModel.kt` (The backing store)
3.  `HiltModel.kt` (The composable abstraction)