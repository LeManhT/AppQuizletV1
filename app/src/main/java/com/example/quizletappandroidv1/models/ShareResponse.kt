package com.example.appquizlet.model

import com.example.quizletappandroidv1.models.FlashCardModel

class ShareResponse(
    val idOwner: String,
    val nameOwner: String,
    val avatarOwner: ByteArray,
    val name: String,
    val timeCreated: Long,
    val description: String,
    val cards: List<FlashCardModel>
) {
}