package com.example.quizletappandroidv1.repository.home

import com.example.quizletappandroidv1.database.api.retrofit.ApiService
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun fetchUserData() {

    }

}