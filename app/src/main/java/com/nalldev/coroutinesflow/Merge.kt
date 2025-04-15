package com.nalldev.coroutinesflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan

class MergeViewModel : ViewModel() {
    val newsFromDB: Flow<List<NewsDB>> = flowOf(
        listOf(
            NewsDB(1, "Title 1", "Description 1"),
            NewsDB(2, "Title 2", "Description 2"),
        ),
        listOf(
            NewsDB(3, "Title 3", "Description 3"),
            NewsDB(4, "Title 4", "Description 4"),
        )
    ).onEach {
        delay(500)
    }

    val newsFromAPI: Flow<List<NewsAPI>> = flowOf(
        listOf(
            NewsAPI(
                1,
                "Title 1",
                "Description 1",
                "11/04/2025",
                "https://image1.png",
            ),
            NewsAPI(
                2,
                "Title 2",
                "Description 2",
                "12/04/2025",
                "https://image2.png",
            ),
            NewsAPI(
                3,
                "Title 3",
                "Description 3",
                "13/04/2025",
                "https://image3.png",
            ),
            NewsAPI(
                4,
                "Title 4",
                "Description 4",
                "14/04/2025",
                "https://image4.png",
            ),
            NewsAPI(
                5,
                "Title 5",
                "Description 5",
                "15/04/2025",
                "https://image5.png",
            ),
            NewsAPI(
                6,
                "Title 6",
                "Description 6",
                "16/04/2025",
                "https://image6.png",
            ),
        ),
        listOf(
            NewsAPI(
                7,
                "Title 7",
                "Description 7",
                "17/04/2025",
                "https://image7.png",
            ),
            NewsAPI(
                8,
                "Title 8",
                "Description 8",
                "18/04/2025",
                "https://image8.png",
            ),
        )
    ).onEach {
        delay(2000)
    }

    val news: Flow<List<NewsUI>> = merge(
        newsFromDB.map { list -> list.map { NewsUI(it.id, it.title, it.category) } },
        newsFromAPI.map { list -> list.map { NewsUI(it.id, it.title, it.category) } }
    )
//        .scan(emptyList()) { oldList, newList -> oldList + newList }
}

@Composable
fun MergeView(modifier: Modifier = Modifier) {
    val viewModel: MergeViewModel = viewModel()
    val newsUI = remember { mutableStateListOf<NewsUI>() }

    LaunchedEffect(Unit) {
        viewModel.news.collect { listNews ->
            listNews.forEach { news ->
                if (newsUI.none { it.id == news.id }) {
                    newsUI.add(news)
                }
            }
        }
    }

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