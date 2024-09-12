package com.example.quizletappandroidv1.viewmodel.user

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.repository.user.UserRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _signUpResult = MutableLiveData<Result<String>>()
    val signUpResult: LiveData<Result<String>> = _signUpResult

    private val _loginResult = MutableLiveData<Result<UserResponse>>()
    val loginResult: LiveData<Result<UserResponse>> = _loginResult

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
            Log.d("userResponse3333", Gson().toJson(result))
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
}