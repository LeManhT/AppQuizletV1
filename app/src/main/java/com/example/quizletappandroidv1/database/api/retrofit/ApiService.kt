package com.example.quizletappandroidv1.database.api.retrofit

import com.example.appquizlet.model.SearchSetModel
import com.example.appquizlet.model.ShareResponse
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.models.CreateSetRequest
import com.example.quizletappandroidv1.models.DetectContinueModel
import com.example.quizletappandroidv1.models.NoticeModel
import com.example.quizletappandroidv1.models.RankResultModel
import com.example.quizletappandroidv1.models.ShareFolderModel
import com.example.quizletappandroidv1.models.StudySetModel
import com.example.quizletappandroidv1.models.admin.UserAdmin
import com.google.gson.JsonObject
import com.squareup.okhttp.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @POST("User/SignUp")
    suspend fun createUser(@Body body: JsonObject): Response<JsonObject>

    @POST("User/Login")
    suspend fun loginUser(@Body body: JsonObject): Response<UserResponse>

    @POST("Folder/Create")
    suspend fun createNewFolder(
        @Query("userId") userId: String,
        @Body body: JsonObject
    ): Response<UserResponse>


    //    @PUT("Folder/Update")
//    fun updateFolder(
//        @Query("userId") userId: String,
//        @Body body: JsonObject
//    ): Call<UserResponse>
//
    @PUT("Folder/UpdateInfo")
    suspend fun updateFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String,
        @Body body: JsonObject
    ): Response<UserResponse>


    @DELETE("Folder/Delete")
    suspend fun deleteFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String
    ): Response<UserResponse>

    @POST("StudySet/Create")
    suspend fun createNewStudySet(
        @Query("userId") userId: String,
        @Body body: CreateSetRequest
    ): Response<UserResponse>

    @DELETE("StudySet/Delete")
    suspend fun deleteStudySet(
        @Query("userId") userId: String,
        @Query("setId") setId: String
    ): Response<UserResponse>

    @PUT("StudySet/UpdateInfo")
    suspend fun updateStudySet(
        @Query("userId") userId: String,
        @Query("setId") setId: String,
        @Body body: CreateSetRequest
    ): Response<UserResponse>

    @POST("Folder/InsertSetExisting")
    suspend fun addSetToFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String,
        @Body body: MutableSet<String>
    ): Response<UserResponse>

    @DELETE("Folder/RemoveSet")
    suspend fun removeSetFromFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String,
        @Query("setId") setId: String
    ): Response<UserResponse>

    @GET("/StudySetPublic/GetOne")
    suspend fun getOneStudySet(
        @Query("setId") setId: String
    ): Response<StudySetModel>

    @POST("User/DetectContinueStudy")
    suspend fun detectContinueStudy(
        @Query("userId") userId: String,
        @Query("timeDetect") timeDetect: Long
    ): Response<DetectContinueModel>

    @GET("StudySet/ShareView")
    suspend fun getSetShareView(
        @Query("userId") userId: String, @Query("setId") setId: String
    ): Response<ShareResponse>

    @GET("Folder/ShareView")
    suspend fun getFolderShareView(
        @Query("userId") userId: String, @Query("folderId") folderId: String
    ): Response<ShareFolderModel>

    @PUT("User/UpdateInfo")
    suspend fun updateUserInfo(
        @Query("userId") userId: String, @Body body: RequestBody
    ): Response<UserResponse>

    @POST("StudySet/EnablePublic")
    suspend fun enablePublicSet(
        @Query("userId") userId: String,
        @Query("setId") setId: String
    )

    @POST("StudySet/DisablePublic")
    suspend fun disablePublicSet(
        @Query("userId") userId: String,
        @Query("setId") setId: String
    )

    @GET("StudySetPublic/Find")
    suspend fun findStudySet(
        @Query("keyword") keyword: String
    ): Response<List<SearchSetModel>>

    @GET("StudySetPublic/GetAll")
    suspend fun getAllSet(
    ): Response<List<SearchSetModel>>

    @POST("StudySet/AddToManyFolders")
    suspend fun addSetToManyFolder(
        @Query("userId") userId: String,
        @Query("setId") setId: String,
        @Body body: MutableSet<String>
    ): Response<UserResponse>

    @PUT("User/UpdateInfo")
    suspend fun updateUserInfoNoImg(
        @Query("userId") userId: String,
        @Body body: JsonObject
    ): Response<UserResponse>

    @PUT("User/ChangePassword")
    suspend fun changePassword(
        @Query("id") id: String,
        @Body body: JsonObject
    ): Response<UserResponse>

    @GET("User/GetRankResult")
    suspend fun getRankResult(
        @Query("userId") userId: String
    ): Response<RankResultModel>


    @GET("User/GetAllCurrentNotices")
    suspend fun getAllCurrentNotices(
        @Query("userId") userId: String
    ): Response<List<NoticeModel>>

    @POST("User/GetInfoByID")
    suspend fun getUserData(
        @Query("ID") userId: String
    ): Response<UserResponse>

    @GET("Admin/GetUsers")
    suspend fun getListUserAdmin(
        @Query("from") from: Int,
        @Query("to") to: Int
    ): Response<List<UserResponse>>

    @POST("Admin/SetSuspendUser")
    suspend fun suspendUser(
        @Query("userId") userId: String,
        @Query("suspend") suspend: Boolean
    )

    @POST("Admin/Login")
    suspend fun loginAdmin(
        @Query("loginName") username: String,
        @Query("password") password: String
    ): Response<UserAdmin>

}