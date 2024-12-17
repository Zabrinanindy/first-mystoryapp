package com.aplikasi.mystory.ui.signUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.data.remote.repository.UserRepository
import com.aplikasi.mystory.data.remote.response.SignUpResponse

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun signUp(username:String, email: String, password: String): LiveData<ResultState<SignUpResponse>> {
        return userRepository.signUp(username, email, password)
    }
}