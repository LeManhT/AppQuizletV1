package com.example.quizletappandroidv1.repository.user

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.quizletappandroidv1.database.api.retrofit.ApiService
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.utils.Helper
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
}