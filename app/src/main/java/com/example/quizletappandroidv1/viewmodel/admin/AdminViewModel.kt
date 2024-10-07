package com.example.quizletappandroidv1.viewmodel.admin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.models.admin.UserAdmin
import com.example.quizletappandroidv1.repository.admin.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(private val adminRepository: AdminRepository) :
    ViewModel() {

    private val _allUser = MutableLiveData<Result<List<UserResponse>>>()
    val allUser: MutableLiveData<Result<List<UserResponse>>> = _allUser

    private val _loginAdminResult = MutableLiveData<Result<UserAdmin>>()
    val loginAdminResult: MutableLiveData<Result<UserAdmin>> = _loginAdminResult

    val pagingUsers = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { adminRepository.getPagingUsers() }
    ).flow.cachedIn(viewModelScope)

//    fun getListUserAdmin(from: Int, to: Int) {
//        viewModelScope.launch {
//            val result = adminRepository.getListUserAdmin(from, to)
//            _allUser.postValue(result)
//        }
//    }

    fun suspendUser(userId: String, suspend: Boolean) {
        viewModelScope.launch {
            adminRepository.suspendUser(userId, suspend)
        }
    }

    fun loginAdmin(username: String, password: String) {
        viewModelScope.launch {
            val result = adminRepository.loginAdmin(username, password)
            _loginAdminResult.postValue(result)
        }
    }

}