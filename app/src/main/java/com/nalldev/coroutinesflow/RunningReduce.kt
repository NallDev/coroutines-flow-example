package com.nalldev.coroutinesflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.stateIn

class RunningReduceViewModel : ViewModel() {
    val newsFromDB: Flow<List<NewsDB>> = flowOf(
        listOf(NewsDB(1, "Title 1", "Comedy"), NewsDB(2, "Title 2", "Comedy")),
        listOf(NewsDB(3, "Title 3", "Politic"), NewsDB(4, "Title 4", "Politic"))
    ).onEach {
        delay(500)
    }

    val newsFromAPI: Flow<List<NewsAPI>> = flowOf(
        listOf(
            NewsAPI(1, "Title 1", "Description 1", "Comedy", "https://image1.png"),
            NewsAPI(2, "Title 2", "Description 2", "Comedy", "https://image2.png"),
            NewsAPI(3, "Title 3", "Description 3", "Politic", "https://image3.png"),
            NewsAPI(4, "Title 4", "Description 4", "Politic", "https://image4.png"),
            NewsAPI(5, "Title 5", "Description 5", "Fashion", "https://image5.png"),
            NewsAPI(6, "Title 6", "Description 6", "Fashion", "https://image6.png"),
        ),
        listOf(
            NewsAPI(7, "Title 7", "Description 7", "Economy", "https://image7.png"),
            NewsAPI(8, "Title 8", "Description 8", "Economy", "https://image8.png"),
        )
    ).onEach {
        delay(2000)
    }

    val allNews: StateFlow<List<NewsUI>> = merge(
        newsFromDB.map { list -> list.map { NewsUI(it.id, it.title, it.category) } },
        newsFromAPI.map { list -> list.map { NewsUI(it.id, it.title, it.category) } }
    ).runningReduce { oldList, newList ->
        val otherNews = newList.filter { newItem ->
            oldList.none { oldItem -> oldItem.id == newItem.id }
        }
        oldList + otherNews
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
}


@Composable
fun RunningReduceScreen(modifier: Modifier = Modifier) {
    val viewModel: RunningReduceViewModel = viewModel()
    val newsUI by viewModel.allNews.collectAsStateWithLifecycle()

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