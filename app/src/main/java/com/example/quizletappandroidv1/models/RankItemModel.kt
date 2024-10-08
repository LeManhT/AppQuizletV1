package com.example.quizletappandroidv1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RankItemModel(
    val score: Int,
    val seqId: Int,
    val userName: String,
    val email: String,
    val dateOfBirth: String,
    var order: Int? = 0
) : Parcelable