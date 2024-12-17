package com.aplikasi.mystory.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.data.pref.UserModel
import com.aplikasi.mystory.data.remote.repository.UserRepository
import com.aplikasi.mystory.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun login(email: String, password: String): LiveData<ResultState<LoginResponse>> {
        return userRepository.login(email, password)
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

}