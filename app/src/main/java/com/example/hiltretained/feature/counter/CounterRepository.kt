package com.example.hiltretained.feature.counter

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterRepository @Inject constructor() {
    private var count = 0

    fun increment(): Int = ++count

    fun decrement(): Int = --count

    fun current(): Int = count
}
