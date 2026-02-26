package com.example.hiltretained.core.retained

import androidx.lifecycle.ViewModel
import java.util.concurrent.ConcurrentHashMap

class RetainedStoreViewModel : ViewModel() {
    private val store = ConcurrentHashMap<String, RetainedComponent>()

    @Suppress("UNCHECKED_CAST")
    fun <T : RetainedComponent> getOrCreate(key: String, factory: () -> T): T {
        return store.getOrPut(key) { factory() } as T
    }

    fun remove(key: String) {
        store.remove(key)?.onCleared()
    }

    override fun onCleared() {
        store.values.forEach { it.onCleared() }
        store.clear()
    }
}
