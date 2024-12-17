package com.aplikasi.mystory.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.data.pref.UserModel
import com.aplikasi.mystory.data.pref.UserPreference
import com.aplikasi.mystory.data.remote.response.LoginResponse
import com.aplikasi.mystory.data.remote.response.SignUpResponse
import com.aplikasi.mystory.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun signUp(name: String, email: String, password: String): LiveData<ResultState<SignUpResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.signUp(name, email, password)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<ResultState<LoginResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(email, password)
            val token = response.loginResult?.token
            if (token != null) {
                userPreference.saveSession(UserModel(email, token, true))
                emit(ResultState.Success(response))
            } else {
                emit(ResultState.Error("Login failed: Token is null"))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
