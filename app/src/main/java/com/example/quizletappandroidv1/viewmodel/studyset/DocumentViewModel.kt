package com.example.quizletappandroidv1.viewmodel.studyset

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.model.SearchSetModel
import com.example.appquizlet.model.ShareResponse
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.models.CreateSetRequest
import com.example.quizletappandroidv1.models.ShareFolderModel
import com.example.quizletappandroidv1.models.StudySetModel
import com.example.quizletappandroidv1.repository.studyset.DocumentRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.okhttp.RequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(private val documentRepository: DocumentRepository) :
    ViewModel() {
    private val _studySets = MutableLiveData<List<StudySetModel>>()
    val studySets: LiveData<List<StudySetModel>> get() = _studySets

    private val _folderResponse = MutableLiveData<UserResponse>()
    val folderResponse: LiveData<UserResponse> get() = _folderResponse

    private val _studySetResponse = MutableLiveData<Boolean>()
    val studySetResponse: LiveData<Boolean> get() = _studySetResponse


    private val _updateStudySetResponse = MutableLiveData<UserResponse>()
    val updateStudySetResponse: LiveData<UserResponse> get() = _updateStudySetResponse

    private val _addSetToFolderResponse = MutableLiveData<UserResponse>()
    val addSetToFolderResponse: LiveData<UserResponse> get() = _addSetToFolderResponse

    private val _removeSetFromFolderResponse = MutableLiveData<UserResponse>()
    val removeSetFromFolderResponse: LiveData<UserResponse> get() = _removeSetFromFolderResponse

    private val _oneStudySet = MutableLiveData<StudySetModel>()
    val oneStudySet: LiveData<StudySetModel> get() = _oneStudySet

    private val _setShareView = MutableLiveData<ShareResponse>()
    val setShareView: LiveData<ShareResponse> get() = _setShareView

    private val _folderShareView = MutableLiveData<ShareFolderModel>()
    val folderShareView: LiveData<ShareFolderModel> get() = _folderShareView

    private val _updateUserInfoResponse = MutableLiveData<UserResponse>()
    val updateUserInfoResponse: LiveData<UserResponse> get() = _updateUserInfoResponse

    private val _studySetSearchResults = MutableLiveData<List<SearchSetModel>>()
    val studySetSearchResults: LiveData<List<SearchSetModel>> get() = _studySetSearchResults

    private val _allStudySets = MutableLiveData<List<SearchSetModel>>()
    val allStudySets: LiveData<List<SearchSetModel>> get() = _allStudySets

    private val _addSetToManyFoldersResponse = MutableLiveData<UserResponse>()
    val addSetToManyFoldersResponse: LiveData<UserResponse> get() = _addSetToManyFoldersResponse

    private val _isPublic = MutableLiveData<Boolean>()
    val isPublic: LiveData<Boolean> = _isPublic


    private val _isFrontSide = MutableLiveData<Boolean>().apply { value = true }  // Default to front side
    val isFrontSide: LiveData<Boolean> get() = _isFrontSide

    // Toggle front/back
    fun toggleFlashcardSide() {
        _isFrontSide.value = _isFrontSide.value?.not()
    }

    fun loadStudySets(userId: String) {
        viewModelScope.launch {
// val sets = studySetRepository.getAllStudySets(userId)
// _studySets.postValue(sets)
        }
    }


    fun updatePublicStatus(isPublic: Boolean) {
        _isPublic.value = isPublic
    }

    fun deleteStudySet(setId: String, userId: String) {
        viewModelScope.launch {
            try {
                documentRepository.deleteStudySet(userId, setId)
            } catch (e: Exception) {
                Timber.tag("Error study set").d(e.message.toString())
            }
        }
    }


    fun createNewFolder(userId: String, body: JsonObject) {
        viewModelScope.launch {
            try {
                val result = documentRepository.createNewFolder(userId, body)
                result.fold(
                    onSuccess = { _folderResponse.postValue(it) },
                    onFailure = { Timber.e(it, "Error creating folder") }
                )
            } catch (e: Exception) {
                Timber.e(e, "Error in createNewFolder")
            }
        }
    }

    fun updateFolder(userId: String, folderId: String, body: JsonObject) {
        viewModelScope.launch {
            try {
                val result = documentRepository.updateFolder(userId, folderId, body)
                result.fold(
                    onSuccess = { _folderResponse.postValue(it) },
                    onFailure = { Timber.e(it, "Error updating folder") }
                )
            } catch (e: Exception) {
                Timber.e(e, "Error in updateFolder")
            }
        }
    }

    fun deleteFolder(userId: String, folderId: String) {
        viewModelScope.launch {
            try {
                val result = documentRepository.deleteFolder(userId, folderId)
                result.fold(
                    onSuccess = { _folderResponse.postValue(it) },
                    onFailure = { Timber.e(it, "Error deleting folder") }
                )
            } catch (e: Exception) {
                Timber.e(e, "Error in deleteFolder")
            }
        }
    }

    fun createNewStudySet(userId: String, body: CreateSetRequest) {
        viewModelScope.launch {
            try {
                val result = documentRepository.createNewStudySet(userId, body)
                result.fold(
                    onSuccess = { _studySetResponse.postValue(true) },
                    onFailure = {
                        _studySetResponse.postValue(false)
                        Timber.e(it, "Error creating StudySet")
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Error in createNewStudySet")
            }
        }
    }


    fun updateStudySet(userId: String, setId: String, request: CreateSetRequest) {
        viewModelScope.launch {
            val result = documentRepository.updateStudySet(userId, setId, request)
            result.fold(
                onSuccess = { _updateStudySetResponse.postValue(it) },
                onFailure = { Timber.e(it, "Error updating study set") }
            )
        }
    }

    fun addSetToFolder(userId: String, folderId: String, setIds: MutableSet<String>) {
        viewModelScope.launch {
            val result = documentRepository.addSetToFolder(userId, folderId, setIds)
            result.fold(
                onSuccess = { _addSetToFolderResponse.postValue(it) },
                onFailure = { Timber.e(it, "Error adding study set to folder") }
            )
        }
    }

    fun removeSetFromFolder(userId: String, folderId: String, setId: String) {
        viewModelScope.launch {
            val result = documentRepository.removeSetFromFolder(userId, folderId, setId)
            result.fold(
                onSuccess = { _removeSetFromFolderResponse.postValue(it) },
                onFailure = { Timber.e(it, "Error removing study set from folder") }
            )
        }
    }

    fun getOneStudySet(setId: String) {
        viewModelScope.launch {
            val result = documentRepository.getOneStudySet(setId)
            result.fold(
                onSuccess = { _oneStudySet.postValue(it) },
                onFailure = { Timber.e(it, "Error fetching study set") }
            )
            Log.d("MMMMMM", Gson().toJson(result))
        }
    }

    fun getSetShareView(userId: String, setId: String) {
        viewModelScope.launch {
            val result = documentRepository.getSetShareView(userId, setId)
            result.fold(
                onSuccess = { _setShareView.postValue(it) },
                onFailure = { Timber.e(it, "Error fetching set share view") }
            )
        }
    }

    fun getFolderShareView(userId: String, folderId: String) {
        viewModelScope.launch {
            val result = documentRepository.getFolderShareView(userId, folderId)
            result.fold(
                onSuccess = { _folderShareView.postValue(it) },
                onFailure = { Timber.e(it, "Error fetching folder share view") }
            )
        }
    }

    fun updateUserInfo(userId: String, body: RequestBody) {
        viewModelScope.launch {
            val result = documentRepository.updateUserInfo(userId, body)
            result.fold(
                onSuccess = { _updateUserInfoResponse.postValue(it) },
                onFailure = { Timber.e(it, "Error updating user info") }
            )
        }
    }

    fun updateUserInfoNoImg(userId: String, body: JsonObject) {
        viewModelScope.launch {
            val result = documentRepository.updateUserInfoNoImg(userId, body)
            result.fold(
                onSuccess = { _updateUserInfoResponse.postValue(it) },
                onFailure = { Timber.e(it, "Error updating user info") }
            )
        }
    }

    fun enablePublicSet(userId: String, setId: String) {
        viewModelScope.launch {
            documentRepository.enablePublicSet(userId, setId)
        }
    }

    fun disablePublicSet(userId: String, setId: String) {
        viewModelScope.launch {
            documentRepository.disablePublicSet(userId, setId)
        }
    }

    fun findStudySet(keyword: String) {
        viewModelScope.launch {
            val result = documentRepository.findStudySet(keyword)
            result.fold(
                onSuccess = { _studySetSearchResults.postValue(it) },
                onFailure = { Timber.e(it, "Error finding study set") }
            )
        }
    }

    fun getAllStudySets() {
        viewModelScope.launch {
            val result = documentRepository.getAllSet()
            result.fold(
                onSuccess = { _allStudySets.postValue(it) },
                onFailure = { Timber.e(it, "Error fetching all study sets") }
            )
        }
    }

    fun addSetToManyFolders(userId: String, setId: String, folderIds: MutableSet<String>) {
        viewModelScope.launch {
            val result = documentRepository.addSetToManyFolders(userId, setId, folderIds)
            result.fold(
                onSuccess = { _addSetToManyFoldersResponse.postValue(it) },
                onFailure = { Timber.e(it, "Error adding set to many folders") }
            )
        }
    }

}