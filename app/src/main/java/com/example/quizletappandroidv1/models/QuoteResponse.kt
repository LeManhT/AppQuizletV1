package com.example.quizletappandroidv1.models

import com.example.quizletappandroidv1.entity.QuoteEntity

data class QuoteResponse(
    val count: Int,
    val totalCount: Int,
    val page: Int,
    val totalPages: Int,
    val lastItemIndex: Int,
    val results: List<QuoteEntity>
)

data class QuoteRemote(
    val _id: String,
    val content: String,
    val author: String,
    val authorSlug: String,
    val length: Int,
    val tags: List<String>
)
