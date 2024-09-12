package com.example.quizletappandroidv1.repository.home

import android.util.Log
import com.example.quizletappandroidv1.database.api.retrofit.ApiService
import com.example.quizletappandroidv1.models.DetectContinueModel
import com.example.quizletappandroidv1.models.RankResultModel
import com.example.quizletappandroidv1.models.TaskData
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun fetchUserData() {

    }


    suspend fun getUserRanking(userId: String): Result<RankResultModel> {
        return try {
            val result = apiService.getRankResult(userId)
            if (result.isSuccessful) {
                result.body()?.let {
                    Log.e("Error get rank 0", "Error response: ${Gson().toJson(it)}")
//                    UserM.setDataRanking(it)
                    Result.success(it)
                } ?: run {
                    val errorMessage = "Response body is null"
                    Timber.e(errorMessage)
                    Result.failure(Exception(errorMessage))
                }
            } else {
                val errorMessage = result.errorBody()?.string() ?: "Unknown error"
                Timber.e("Get User Ranking error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception occurred while fetching user ranking")
            Result.failure(e)
        }
    }


    suspend fun getDataAchievement(userId: String, timeDetect: Long): Result<DetectContinueModel> {
        return try {
            val result = apiService.detectContinueStudy(userId, timeDetect)
            if (result.isSuccessful) {
                result.body()?.let {
                    Result.success(it)
                } ?: run {
                    val errorMessage = "Response body is null"
                    Timber.e(errorMessage)
                    Result.failure(Exception(errorMessage))
                }
            } else {
                val errorMessage = result.errorBody()?.string() ?: "Unknown error"
                Timber.e("Get User Ranking error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception occurred while fetching achievement data")
            Result.failure(Exception(e))
        }
    }
}