package com.nalldev.coroutinesflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class MapViewModel : ViewModel() {
    private val responseNewsAPI: Flow<List<NewsAPI>> = flowOf(
        listOf(
            NewsAPI(1, "Title 1", "Description 1", "Comedy", "https://image1.png"),
            NewsAPI(2, "Title 2", "Description 2", "Comedy", "https://image2.png"),
            NewsAPI(3, "Title 3", "Description 3", "Politic", "https://image3.png"),
            NewsAPI(4, "Title 4", "Description 4", "Politic", "https://image4.png"),
            NewsAPI(5, "Title 5", "Description 5", "Fashion", "https://image5.png"),
            NewsAPI(6, "Title 6", "Description 6", "Fashion", "https://image6.png"),
        ),
    )

    val news: Flow<List<NewsUI>> =
        responseNewsAPI.onEach { delay(2000) }
            .map { listNewsAPI ->
                listNewsAPI.map { news ->
                    NewsUI(
                        news.id,
                        news.title,
                        news.category
                    )
                }
            }
}

@Composable
fun MapView(modifier: Modifier = Modifier) {
    val viewModel: MapViewModel = viewModel()
    val newsUI by viewModel.news.collectAsStateWithLifecycle(emptyList())

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(newsUI) { news ->
            Card {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = news.title)
                    Text(text = news.category)
                }
            }
        }
    }
}