package com.example.hiltretained.feature.counter

import android.util.Log
import com.example.hiltretained.core.retained.RetainedComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class CounterPresenter @AssistedInject constructor(
    repository: CounterRepository,
    @Assisted private val counterId: String,
) : RetainedComponent {
    private val counter = repository.createCounter()
    val count = counter.count

    fun increment() {
        counter.increment()
    }

    fun decrement() {
        counter.decrement()
    }

    override fun onCleared() {
        Log.d("CounterPresenter", "Destroyed: $counterId")
    }

    @AssistedFactory
    interface Factory {
        fun create(counterId: String): CounterPresenter
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Entry {
        fun factory(): Factory
    }
}
