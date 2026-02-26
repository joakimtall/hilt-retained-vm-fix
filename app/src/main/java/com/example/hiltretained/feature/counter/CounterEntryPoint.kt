package com.example.hiltretained.feature.counter

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface CounterEntryPoint {
    fun counterPresenterFactory(): CounterPresenter.Factory
}
