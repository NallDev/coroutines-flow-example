package com.nalldev.coroutinesflow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class CombineViewModel : ViewModel() {
    val username: StateFlow<String>
        field = MutableStateFlow("")

    val password: StateFlow<String>
        field = MutableStateFlow("")

    val isUsernameValid =
        username.debounce(0.5.seconds.inWholeMilliseconds).map { it.length >= 8 }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val isPasswordValid =
        password.debounce(0.5.seconds.inWholeMilliseconds).map { it.length >= 8 }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val isAllFormValid: StateFlow<Boolean> =
        combine(isUsernameValid, isPasswordValid) { isUsernameValid, isPasswordValid ->
            isUsernameValid && isPasswordValid
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    fun onUsernameChange(username: String) {
        this.username.value = username
    }

    fun onPasswordChange(password: String) {
        this.password.value = password
    }
}




@Composable
fun CombineScreen(modifier: Modifier = Modifier) {
    val viewModel: CombineViewModel = viewModel()
    val isAllFormValid by viewModel.isAllFormValid.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val isUsernameValid by viewModel.isUsernameValid.collectAsStateWithLifecycle()
    val isPasswordValid by viewModel.isPasswordValid.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        OutlinedTextField(
            value = username,
            onValueChange = viewModel::onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") },
            isError = !isUsernameValid,
            supportingText = {
                if (!isUsernameValid) {
                    Text("Username must be at least 8 characters")
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
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
        Spacer(modifier = Modifier.height(16.dp))
        ElevatedButton(onClick = {}, enabled = isAllFormValid, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Register")
        }
    }
}