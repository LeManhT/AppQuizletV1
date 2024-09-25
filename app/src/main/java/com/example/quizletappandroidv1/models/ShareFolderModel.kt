package com.example.quizletappandroidv1.models

class ShareFolderModel(
    val nameOwner: String,
    val avatarOwner: ByteArray,
    val name: String,
    val timeCreated: Long,
    val description: String,
    val studySets: List<StudySetModel>
)
