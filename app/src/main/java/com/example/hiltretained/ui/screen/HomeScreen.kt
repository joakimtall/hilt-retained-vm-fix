package com.example.hiltretained.ui.screen

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hiltretained.core.retained.hiltRetained
import com.example.hiltretained.feature.counter.CounterEntryPoint
import com.example.hiltretained.feature.counter.CounterPresenter
import dagger.hilt.android.EntryPointAccessors

@Composable
fun HomeScreen(onNavigateToDetails: () -> Unit) {
    val activity = LocalContext.current as Activity
    val entryPoint = EntryPointAccessors.fromActivity(activity, CounterEntryPoint::class.java)
    var showCounter by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(onClick = { showCounter = !showCounter }) {
            Text(if (showCounter) "Hide Counter" else "Show Counter")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (showCounter) {
            CounterContent(entryPoint)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToDetails) {
            Text("Go to Details")
        }
    }
}

@Composable
private fun CounterContent(entryPoint: CounterEntryPoint) {
    val presenter = hiltRetained(key = "counter") {
        entryPoint.counterPresenterFactory().create("counter")
    }
    val count by presenter.count.collectAsState()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Count: $count", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { presenter.decrement() }) { Text("-") }
            Button(onClick = { presenter.increment() }) { Text("+") }
        }
    }
}
