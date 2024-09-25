package com.example.quizletappandroidv1.models.admin

import com.google.gson.annotations.SerializedName

data class UserAdmin(
    @SerializedName("id")
    val id: String,
    @SerializedName("seqId")
    val seqId: Int,
    @SerializedName("loginName")
    val loginName: String,
    @SerializedName("loginPassword")
    val loginPassword: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("dateOfBirth")
    val dateOfBirth: String,
    @SerializedName("timeCreated")
    val timeCreated: Long,
)
