package com.example.quizletappandroidv1.listener

import com.example.quizletappandroidv1.models.FlashCardModel


interface LearnCardClick {
    fun handleLearnCardClick(position: Int, cardItem: FlashCardModel)
}