package com.example.quizletappandroidv1.repository.admin

import com.example.quizletappandroidv1.database.api.retrofit.ApiService
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.models.admin.UserAdmin
import timber.log.Timber
import javax.inject.Inject

class AdminRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getListUserAdmin(from: Int, to: Int): Result<List<UserResponse>> {
        return try {
            val response = apiService.getListUserAdmin(from, to)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load users"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Unexpected exception occurred while logging in user")
            Result.failure(e)
        }
    }

    suspend fun suspendUser(userId: String, suspend: Boolean) {
        apiService.suspendUser(userId, suspend)
    }

    suspend fun loginAdmin(username: String, password: String): Result<UserAdmin> {
        return try {
            val response = apiService.loginAdmin(username, password)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to login admin"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}