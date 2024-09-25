package com.example.appquizlet.model

import com.example.quizletappandroidv1.models.FlashCardModel

class SearchSetModel(
    val id: String,
    val idOwner: String,
    val nameOwner: String,
    val timeCreated: Long,
    val countTerm: Int,
    val name: String,
    val description: String,
    val allCards: List<FlashCardModel>
) {
}