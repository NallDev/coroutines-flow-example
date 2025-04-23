package com.nalldev.coroutinesflow

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn


class DistinctUntilChangedViewModel : ViewModel() {
    val fileDownloaded: Flow<Double> =
        flowOf(1.0, 1.12, 10.0, 10.11, 10.46, 15.0, 30.0, 30.5, 30.6, 31.4, 40.0, 80.0, 85.0, 100.0)
            .onEach {
                delay(500L)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = 0.0
            )

    val downloadProgress = fileDownloaded.map { currentDownloadProgress ->
        currentDownloadProgress.toInt()
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = 0
    )
}



@Composable
fun DistinctUntilChangedScreen(modifier: Modifier = Modifier) {
    val viewModel: DistinctUntilChangedViewModel = viewModel()
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
        modifier = modifier,
        strokeWidth = 8.dp
    )
}