package com.nalldev.coroutinesflow

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatchViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<String>>(emptyList())
    val products: StateFlow<List<String>> = _products

    private val _categoryProductsResult = MutableStateFlow<List<String>>(emptyList())
    val categoryProductsResult: StateFlow<List<String>> = _categoryProductsResult

    var counter = 0

    init {
        viewModelScope.launch {
            flowOf(
                "Telur Label-001",
                "Telur Label-002",
                "Telur Label-003",
                "Telur Label-004",
                "Telur Label-005",
                "Telur Label-006",
                "Telur Label-007"
            )
                .onEach { product ->
                    _products.update { currentProducts ->
                        currentProducts + product
                    }
                }
                .map { egg ->
                    val category = listOf("Super", "Besar", "Biasa", "Kecil", "Pecah")
                    "$egg | ${category[counter++]}"
                }
                .catch { throwable ->
                    if (throwable is IndexOutOfBoundsException) {
                        println("Error from array")
                    } else {
                        println("Unknown Error")
                    }
                }
                .collect { result ->
                    _categoryProductsResult.update { currentCategoryProductsResult ->
                        currentCategoryProductsResult + result
                    }
                }
        }
    }
}


@Composable
fun CatchScreen(modifier: Modifier = Modifier) {
    val viewModel: CatchViewModel = viewModel()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val categoryProductsResult by viewModel.categoryProductsResult.collectAsStateWithLifecycle()


    println(categoryProductsResult)
    LazyColumn(modifier = modifier) {
        items(products, key = { it }) { product ->
            Text(product)
        }
        items(categoryProductsResult, key = { it }) { productResult ->
            Text(productResult)
        }
    }
}