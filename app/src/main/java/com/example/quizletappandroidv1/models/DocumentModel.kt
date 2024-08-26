package com.example.quizletappandroidv1.models

data class DocumentModel(
    val folders: List<FolderModel>,
    val studySets: List<StudySetModel>,
    val flashCards: List<FlashCardModel>
) {
}