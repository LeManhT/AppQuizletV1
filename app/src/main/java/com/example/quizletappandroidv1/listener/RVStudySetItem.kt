package com.example.appquizlet.interfaceFolder

import com.example.quizletappandroidv1.models.StudySetModel


interface RVStudySetItem {

    fun handleClickStudySetItem(setItem: StudySetModel,position : Int)
}