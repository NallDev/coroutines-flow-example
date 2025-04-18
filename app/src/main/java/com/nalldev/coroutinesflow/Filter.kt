package com.nalldev.coroutinesflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlin.time.Duration.Companion.seconds

data class Person(
    val id: Int,
    val name: String,
    val time: String
)

class FilterViewModel : ViewModel() {
    private val person = flowOf(
        Person(1, "Nal", "07:33"),
        Person(2, "Ucup", "08:01"),
        Person(3, "April", "08:05"),
        Person(4, "Stefan", "07:50")
    ).onEach {
        delay(2.seconds.inWholeMilliseconds)
    }

    val latePeople: StateFlow<List<Person>>
        field = MutableStateFlow(emptyList())

    val onTimePeople: StateFlow<List<Person>>
        field = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            launch {
                person.filter {
                    LocalTime.parse(it.time) > LocalTime.parse("08:00")
                }.collect { person ->
                    latePeople.value = latePeople.value + person
                }
            }

            launch {
                person.filter {
                    LocalTime.parse(it.time) <= LocalTime.parse("08:00")
                }.collect { person ->
                    onTimePeople.value = onTimePeople.value + person
                }
            }
        }
    }
}

@Composable
fun FilterView(modifier: Modifier = Modifier) {
    val viewModel: FilterViewModel = viewModel()

    val latePeople by viewModel.latePeople.collectAsStateWithLifecycle()
    val onTimePeople by viewModel.onTimePeople.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(text = "On Time")
        }
        items(onTimePeople, key = { person -> person.id }) { person ->
            Text(text = person.name)
        }
        item {
            Text(text = "Late")
        }
        items(latePeople, key = { person -> person.id }) { person ->
            Text(text = person.name)
        }
    }
}