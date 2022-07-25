package com.example.schoter_app.model

data class NewsApi(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)