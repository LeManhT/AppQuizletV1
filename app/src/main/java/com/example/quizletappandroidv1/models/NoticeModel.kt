package com.example.quizletappandroidv1.models

data class NoticeModel(
    val id: Int,
    val wasPushed: Boolean,
    val title: String,
    val detail: String
)