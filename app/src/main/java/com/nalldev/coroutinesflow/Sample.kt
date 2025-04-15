package com.nalldev.coroutinesflow

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn

class SampleViewModel : ViewModel() {
    val fileDownloaded = flowOf(1, 10, 15, 30, 40, 45, 50, 60, 70, 75, 80, 85, 100)
        .onEach {
            delay(500L)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    @OptIn(FlowPreview::class)
    val downloadProgress = fileDownloaded.sample(1000L).onEach {
        // Value emitted : 1, 15, 40, 70, 80, 100
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0
    )
}


@Composable
fun SampleView(modifier: Modifier = Modifier) {
    val viewModel: SampleViewModel = viewModel()
    val downloadProgress by viewModel.downloadProgress.collectAsStateWithLifecycle()

    val progressAnimationValue by animateFloatAsState(
        targetValue = downloadProgress / 100f,
        animationSpec = tween(
            easing = FastOutLinearInEasing,
            durationMillis = 1000,
            delayMillis = 0
        )
    )

    CircularProgressIndicator(
        progress = { progressAnimationValue },
        modifier = modifier
    )
}