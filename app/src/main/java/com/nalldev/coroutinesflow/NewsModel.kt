package com.nalldev.coroutinesflow

data class NewsDB(
    val id: Int,
    val title: String,
    val category: String
)

data class NewsAPI(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val imageUrl: String,
)

data class NewsUI(
    val id: Int,
    val title: String,
    val category: String
)