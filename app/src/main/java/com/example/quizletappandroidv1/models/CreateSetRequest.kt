package com.example.quizletappandroidv1.models

data class CreateSetRequest(
    val name: String,
    val description: String,
    val idFolderOwner: String? = "",
    val allNewCards: List<FlashCardModel>,
    var isPublish: Boolean? = false
)
