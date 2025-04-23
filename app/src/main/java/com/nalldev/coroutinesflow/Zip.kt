package com.nalldev.coroutinesflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

data class Question(
    val question: String,
    val correctAnswer: Boolean
)

class ZipViewModel : ViewModel() {
    private val questions = flowOf(
        Question("Apakah bendera indonesia berwarna merah putih?", true),
        Question("Apakah jakarta merupakan ibu kota indonesia?", true),
        Question("Apakah kabupaten bandung merupakan ibu kota dari provinsi jawa barat?", false),
        Question("Apakah gunung tangkuban perahu terletak di kota bandung?", false),
        Question("Apakah yogyakarta merupakan daerah istimewa dari Indonesia?", true),
        null
    )

    val currentQuestion : StateFlow<Question?>
        field = MutableStateFlow(null)

    private val userAnswer = MutableSharedFlow<Boolean>()

    val score: StateFlow<Int> =
        questions
            .onEach { question ->
                currentQuestion.value = question
            }.zip(userAnswer) { question, answer ->
                if (question?.correctAnswer == answer) 20 else 0
            }
            .runningReduce { acc, inc -> acc + inc }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun submit(answer: Boolean) = viewModelScope.launch {
        userAnswer.emit(answer)
    }
}

@Composable
fun ZipScreen(modifier: Modifier = Modifier) {
    val viewModel: ZipViewModel = viewModel()
    val question by viewModel.currentQuestion.collectAsStateWithLifecycle()
    val score by viewModel.score.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        question?.let {
            Text(
                text = question?.question.orEmpty()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.submit(true) },
                    modifier = Modifier.weight(1f)
                ) { Text("Ya") }

                Button(
                    onClick = { viewModel.submit(false) },
                    modifier = Modifier.weight(1f)
                ) { Text("Tidak") }
            }
        } ?: run {
            Text("Selesai")
        }

        Spacer(Modifier.height(48.dp))

        Text("Skor: $score")
    }
}

