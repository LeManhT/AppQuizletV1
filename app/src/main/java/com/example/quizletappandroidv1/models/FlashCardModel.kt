package com.example.quizletappandroidv1.models

import android.net.Uri
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class FlashCardModel(
    var id: String? = "",
    var term: String? = "",
    var definition: String? = "",
    val timeCreated: Long? = 120,
    var imageUri: Uri? = null, // Thêm trường imageUri để lưu URI của ảnh
    val isPublic: Boolean? = true,
    @SerializedName("idSetOwner")
    val setOwnerId: String? = "",
    val isSelected: Boolean? = false,
    var isUnMark: Boolean? = false,
    var isNew: Boolean? = false,
    var isAnswer: Boolean? = false
) : Parcelable