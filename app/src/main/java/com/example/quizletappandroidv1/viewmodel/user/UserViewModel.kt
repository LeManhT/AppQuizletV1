package com.example.quizletappandroidv1.viewmodel.user

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    ) : ViewModel() {


    private val _signUpResult = MutableLiveData<Result<String>>()
    val signUpResult: LiveData<Result<String>> = _signUpResult

    private val _loginResult = MutableLiveData<Result<UserResponse>>()
    val loginResult: LiveData<Result<UserResponse>> = _loginResult

    private val _userData = MutableLiveData<Result<UserResponse>>()
    val userData: LiveData<Result<UserResponse>> = _userData

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun createUser(email: String, pass: String, dob: String) {
        viewModelScope.launch {
            val result = userRepository.createUser(email, pass, dob)
            _signUpResult.postValue(result)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun loginUser(email: String, pass: String) {
        viewModelScope.launch {
            val result = userRepository.loginUser(email, pass)
            _loginResult.postValue(result)
        }
    }

    fun changePassword(userId: String, oldPass: String, newPass: String) {
        viewModelScope.launch {
            val result = userRepository.changePassword(
                userId,
                oldPass,
                newPass
            )
            _loginResult.postValue(result)
        }
    }

    fun getUserData(userId: String) {
        viewModelScope.launch {
            val result = userRepository.getUserData(userId)
            Log.d("fd111111", userId)
            _userData.postValue(result)

        }
    }
}