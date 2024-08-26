package com.example.quizletappandroidv1.models

import com.google.gson.annotations.SerializedName

data class StudySetModel(
    val id: String,
    val name: String,
    val timeCreated: Long,
    @SerializedName("idFolderOwner")
    val folderOwnerId: String,
    val description: String,
    val countTerm: Int? = 2,
    val cards: List<FlashCardModel>,
    var isSelected: Boolean? = false,
    @SerializedName("isPublic")
    val isPublic: Boolean? = false,
    val nameOwner: String
)