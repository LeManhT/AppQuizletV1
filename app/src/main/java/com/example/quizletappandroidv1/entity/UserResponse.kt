package com.example.quizletappandroidv1.entity
import com.example.quizletappandroidv1.models.AchievementData
import com.example.quizletappandroidv1.models.DocumentModel
import com.example.quizletappandroidv1.models.StreakData
import com.google.gson.annotations.SerializedName

class UserResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("seqId")
    val seqId: Int,
    @SerializedName("loginName")
    val loginName: String,
    @SerializedName("loginPassword")
    val loginPassword: String,
    @SerializedName("isSuspend")
    val isSuspend: Boolean,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("dateOfBirth")
    val dateOfBirth: String,
    @SerializedName("timeCreated")
    val timeCreated: Long,
    @SerializedName("documents")
    val documents: DocumentModel,
//    @SerializedName("setting")
//    val setting: SettingModel,

//    @SerializedName("avatar") val avatar: ByteArray
    @SerializedName("streak")
    val streak: StreakData,
    @SerializedName("achievement")
    val achievement: AchievementData
)
