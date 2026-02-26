package com.example.hiltretained.feature.counter

import android.util.Log
import com.example.hiltretained.core.retained.RetainedComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CounterPresenter @AssistedInject constructor(
    private val repository: CounterRepository,
    @Assisted private val counterId: String,
) : RetainedComponent {
    private val _count = MutableStateFlow(repository.current())
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        _count.value = repository.increment()
    }

    fun decrement() {
        _count.value = repository.decrement()
    }

    override fun onCleared() {
        Log.d("CounterPresenter", "Counter $counterId destroyed")
    }

    @AssistedFactory
    interface Factory {
        fun create(counterId: String): CounterPresenter
    }
}
