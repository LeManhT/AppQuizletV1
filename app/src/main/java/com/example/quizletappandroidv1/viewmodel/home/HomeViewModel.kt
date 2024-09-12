package com.example.quizletappandroidv1.viewmodel.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.models.DetectContinueModel
import com.example.quizletappandroidv1.models.RankResultModel
import com.example.quizletappandroidv1.models.RankSystem
import com.example.quizletappandroidv1.repository.home.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val homeRepository: HomeRepository) : ViewModel() {

    private val _userInfo = MutableLiveData<UserResponse>()
    val userInfo: LiveData<UserResponse> get() = _userInfo

    private val _rankSystem = MutableLiveData<RankSystem>()
    val rankSystem: LiveData<RankSystem> get() = _rankSystem
    val userId = MyApplication.userId

    private val _dataAchievement = MutableLiveData<Result<DetectContinueModel>>()
    val dataAchievement: LiveData<Result<DetectContinueModel>> get() = _dataAchievement

    private val _rankResult = MutableLiveData<Result<RankResultModel>>()
    val rankResult: LiveData<Result<RankResultModel>> get() = _rankResult



    init {
        if (userId != null) {
            Log.d("userId", userId)
            getUserRanking(userId)
        }
    }

    private fun getUserRanking(userId: String) {
        viewModelScope.launch {
            try {
                val result = homeRepository.getUserRanking(userId)
                result.fold(
                    onSuccess = { rankingData ->
                        // Handle success, rankingData is the UserResponse
                        _rankResult.postValue(result)
                        Timber.tag("Success").d("Ranking data: %s", rankingData)
                    },
                    onFailure = { error ->
                        Timber.tag("Error get rank").d(error.message.toString())
                    }
                )
            } catch (e: Exception) {
                Timber.tag("Error get rank").d(e.message.toString())
            }
        }
    }

    fun getDataAchievement(userId: String, timeDetect: Long) {
        viewModelScope.launch {
            try {
                val result = homeRepository.getDataAchievement(userId, timeDetect)
                _dataAchievement.postValue(result)
            } catch (e: Exception) {
                Timber.tag("Error get rank").d(e.message.toString())
            }
        }
    }

}