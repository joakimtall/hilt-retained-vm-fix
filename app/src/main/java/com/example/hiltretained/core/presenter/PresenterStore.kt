package com.example.hiltretained.core.presenter

import androidx.lifecycle.ViewModel
import java.util.concurrent.ConcurrentHashMap

class PresenterStore : ViewModel() {
    private val presenters = ConcurrentHashMap<String, Presenter>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Presenter> getOrCreate(key: String, factory: () -> T): T {
        return presenters.getOrPut(key) { factory() } as T
    }

    fun remove(key: String) {
        presenters.remove(key)?.onCleared()
    }

    override fun onCleared() {
        presenters.values.forEach { it.onCleared() }
        presenters.clear()
    }
}
