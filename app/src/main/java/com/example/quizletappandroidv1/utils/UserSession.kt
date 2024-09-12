package com.example.quizletappandroidv1.utils

import android.content.Context

object UserSession {
    var userId: String? = null

    fun loadUserId(context: Context) {
        val sharedPreferences = context.getSharedPreferences("idUser", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("key_userid", null)
    }

    fun saveUserId(context: Context, userId: String) {
        val sharedPreferences = context.getSharedPreferences("idUser", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("key_userid", userId)
        editor.apply()
        this.userId = userId
    }
}