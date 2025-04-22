package com.nalldev.coroutinesflow

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

class DebounceViewModel : ViewModel() {
    val password: StateFlow<String>
        field = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    val isPasswordValid =
        password.debounce(0.5.seconds.inWholeMilliseconds).map { it.length >= 8 }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun onPasswordChange(password: String) {
        this.password.value = password
    }
}

@Composable
fun DebounceScreen(modifier: Modifier = Modifier) {
    val viewModel: DebounceViewModel = viewModel()
    val isPasswordValid by viewModel.isPasswordValid.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()

    OutlinedTextField(
        value = password,
        onValueChange = viewModel::onPasswordChange,
        modifier = modifier,
        label = { Text("Password") },
        isError = !isPasswordValid,
        supportingText = {
            if (!isPasswordValid) {
                Text("Password must be at least 8 characters")
            }
        },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}