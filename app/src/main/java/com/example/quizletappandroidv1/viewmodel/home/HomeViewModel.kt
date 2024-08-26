package com.example.quizletappandroidv1.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizletappandroidv1.database.api.retrofit.ApiService
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.models.RankSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _userInfo = MutableLiveData<UserResponse>()
    val userInfo: LiveData<UserResponse> get() = _userInfo

    private val _rankSystem = MutableLiveData<RankSystem>()
    val rankSystem: LiveData<RankSystem> get() = _rankSystem

    init {
        fetchRankingData()
    }

    private fun fetchRankingData() {
        viewModelScope.launch {

        }
    }

    private fun fetchUserDate() {
        viewModelScope.launch {

        }
    }


}