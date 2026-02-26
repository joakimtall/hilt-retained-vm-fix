package com.example.hiltretained.feature.counter

import android.util.Log
import com.example.hiltretained.core.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CounterPresenter @AssistedInject constructor(
    repository: CounterRepository,
    @Assisted private val assistedString: String,
) : Presenter() {
    private val counter = repository.createCounter()
    val count = counter.count

    init {
        presenterScope.launch {
            try {
                while (true) {
                    delay(1000)
                    Log.d(TAG, "[$assistedString] tick (count=${count.value})")
                }
            } catch (e: CancellationException) {
                Log.d(TAG, "[$assistedString] coroutine cancelled")
                throw e
            }
        }
    }

    fun increment() {
        counter.increment()
    }

    fun decrement() {
        counter.decrement()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "[$assistedString] destroyed")
    }

    companion object {
        private const val TAG = "CounterPresenter"
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
