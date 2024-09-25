package com.example.quizletappandroidv1.repository.user

import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import com.example.quizletappandroidv1.database.api.retrofit.ApiService
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.utils.Helper
import com.google.gson.Gson
import com.google.gson.JsonObject
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun createUser(email: String, pass: String, dob: String): Result<String> {
        return try {
            val body = JsonObject().apply {
                addProperty("loginName", email)
                addProperty("loginPassword", pass)
                addProperty(
                    "dateOfBirth", Helper.formatDateSignup(dob)

                )
                addProperty("email", email)
            }
            val result = apiService.createUser(body)
            if (result.isSuccessful) {
                Result.success("Sign-up successful")
            } else {
                val errorMessage = result.errorBody()?.string() ?: "Unknown error"
                Log.d("ERRRORRR", errorMessage.toString())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Timber.e(e, "IOException occurred while creating user")
            Result.failure(e)
        } catch (e: HttpException) {
            Timber.e(e, "HttpException occurred while creating user")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected exception occurred while creating user")
            Result.failure(e)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun loginUser(email: String, pass: String): Result<UserResponse> {
        return try {
            val body = JsonObject().apply {
                addProperty("loginName", email)
                addProperty("loginPassword", pass)
            }
            val result = apiService.loginUser(body)
            if (result.isSuccessful) {
                val userResponse = result.body()!!
                Result.success(userResponse)
            } else {
                val errorMessage = result.errorBody()?.string() ?: "Unknown error"
                Timber.e("Login error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: IOException) {
            Timber.e(e, "IOException occurred while logging in user")
            Result.failure(e)
        } catch (e: HttpException) {
            Timber.e(e, "HttpException occurred while logging in user")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Unexpected exception occurred while logging in user")
            Result.failure(e)
        }
    }

    suspend fun changeEmail(userId: String, newEmail: String): Result<UserResponse> {
        return try {
            val body = JsonObject().apply { addProperty("email", newEmail) }
            val response = apiService.updateUserInfoNoImg(userId, body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(
        userId: String,
        oldPass: String,
        newPass: String
    ): Result<UserResponse> {
        return try {
            val body = JsonObject().apply {
                addProperty(
                    "oldPassword",
                    oldPass
                )
                addProperty("newPassword", newPass)
            }
            val result =
                apiService.changePassword(userId, body)
            if (result.isSuccessful) {
                Result.success(result.body()!!)
            } else {
                Result.failure(Exception(result.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserData(userId: String): Result<UserResponse> {
        return try {
            Timber.tag("API_REQUEST").d("UserID: $userId")
            val result = apiService.getUserData(userId)

            if (result.isSuccessful) {
                Log.d("APIPPPP",Gson().toJson(result.body()!!))
                Result.success(result.body()!!)
            } else {
                val errorBody = result.errorBody()?.string() ?: "No error body"
                Timber.tag("API_ERROR").e("Error: $errorBody, Code: ${result.code()}")
                Log.d("APIPPPP1",Gson().toJson(result.body()!!) + "errroe")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Timber.tag("API_EXCEPTION").e("Exception: ${e.message}")
            Log.d("APIPPPP2",Gson().toJson(e.message) + "errroe")
            Result.failure(e)
        }
    }

}


