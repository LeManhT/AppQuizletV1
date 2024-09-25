package com.example.quizletappandroidv1.repository.studyset

import android.util.Log
import com.example.appquizlet.model.SearchSetModel
import com.example.quizletappandroidv1.models.ShareFolderModel
import com.example.appquizlet.model.ShareResponse
import com.example.quizletappandroidv1.database.api.retrofit.ApiService
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.models.CreateSetRequest
import com.example.quizletappandroidv1.models.StudySetModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.okhttp.RequestBody
import timber.log.Timber
import javax.inject.Inject

class DocumentRepository @Inject constructor(val apiService: ApiService) {

// suspend fun getStudySets(userId: String): List<StudySetModel> {
// return try {
// apiService.getAllStudySets(userId)
// } catch (e: Exception) {
// // Trong trường hợp có lỗi, trả về danh sách rỗng
// emptyList()
// }
// }

    suspend fun deleteStudySet(userId: String, setId: String): Result<UserResponse> {
        return try {
            val response = apiService.deleteStudySet(userId, setId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.e("Error get rank 0", "Error response: ${Gson().toJson(it)}")
                    Result.success(it)
                } ?: run {
                    val errorMessage = "Response body is null"
                    Timber.e(errorMessage)
                    Result.failure(Exception(errorMessage))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Timber.e("Get User Ranking error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception occurred while fetching user ranking")
            Result.failure(e)
        }
    }

    suspend fun createNewFolder(userId: String, body: JsonObject): Result<UserResponse> {
        return try {
            val response = apiService.createNewFolder(userId, body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create folder"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception in createNewFolder")
            Result.failure(e)
        }
    }

    suspend fun updateFolder(
        userId: String,
        folderId: String,
        body: JsonObject
    ): Result<UserResponse> {
        return try {
            val response = apiService.updateFolder(userId, folderId, body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update folder"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception in updateFolder")
            Result.failure(e)
        }
    }

    suspend fun deleteFolder(userId: String, folderId: String): Result<UserResponse> {
        return try {
            val response = apiService.deleteFolder(userId, folderId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to delete folder"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception in deleteFolder")
            Result.failure(e)
        }
    }

    suspend fun createNewStudySet(userId: String, body: CreateSetRequest): Result<UserResponse> {
        return try {
            val response = apiService.createNewStudySet(userId, body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create StudySet"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception in createNewStudySet")
            Result.failure(e)
        }
    }

    suspend fun updateStudySet(
        userId: String,
        setId: String,
        request: CreateSetRequest
    ): Result<UserResponse> {
        return try {
            val response = apiService.updateStudySet(userId, setId, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating study set")
            Result.failure(e)
        }
    }

    suspend fun addSetToFolder(
        userId: String,
        folderId: String,
        setIds: MutableSet<String>
    ): Result<UserResponse> {
        return try {
            val response = apiService.addSetToFolder(userId, folderId, setIds)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error adding study set to folder")
            Result.failure(e)
        }
    }

    suspend fun removeSetFromFolder(
        userId: String,
        folderId: String,
        setId: String
    ): Result<UserResponse> {
        return try {
            val response = apiService.removeSetFromFolder(userId, folderId, setId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error removing study set from folder")
            Result.failure(e)
        }
    }

    suspend fun getOneStudySet(setId: String): Result<StudySetModel> {
        return try {
            val response = apiService.getOneStudySet(setId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting study set")
            Result.failure(e)
        }
    }

    suspend fun getSetShareView(userId: String, setId: String): Result<ShareResponse> {
        return try {
            val response = apiService.getSetShareView(userId, setId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching study set share view")
            Result.failure(e)
        }
    }

    suspend fun getFolderShareView(userId: String, folderId: String): Result<ShareFolderModel> {
        return try {
            val response = apiService.getFolderShareView(userId, folderId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching folder share view")
            Result.failure(e)
        }
    }

    suspend fun updateUserInfo(userId: String, body: RequestBody): Result<UserResponse> {
        return try {
            val response = apiService.updateUserInfo(userId, body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating user info")
            Result.failure(e)
        }
    }

    suspend fun updateUserInfoNoImg(userId: String, body: JsonObject): Result<UserResponse> {
        return try {
            val response = apiService.updateUserInfoNoImg(userId, body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating user info")
            Result.failure(e)
        }
    }

    suspend fun enablePublicSet(userId: String, setId: String) {
        try {
            apiService.enablePublicSet(userId, setId)
        } catch (e: Exception) {
            Timber.e(e, "Error enabling public set")
        }
    }

    suspend fun disablePublicSet(userId: String, setId: String) {
        try {
            apiService.disablePublicSet(userId, setId)
        } catch (e: Exception) {
            Timber.e(e, "Error disabling public set")
        }
    }

    suspend fun findStudySet(keyword: String): Result<List<SearchSetModel>> {
        return try {
            val response = apiService.findStudySet(keyword)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error finding study set")
            Result.failure(e)
        }
    }

    suspend fun getAllSet(): Result<List<SearchSetModel>> {
        return try {
            val response = apiService.getAllSet()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching all study sets")
            Result.failure(e)
        }
    }

    suspend fun addSetToManyFolders(
        userId: String,
        setId: String,
        folderIds: MutableSet<String>
    ): Result<UserResponse> {
        return try {
            val response = apiService.addSetToManyFolder(userId, setId, folderIds)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error adding set to many folders")
            Result.failure(e)
        }
    }

}