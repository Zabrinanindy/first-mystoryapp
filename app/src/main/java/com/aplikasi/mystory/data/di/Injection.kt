package com.aplikasi.mystory.data.di

import android.content.Context
import com.aplikasi.mystory.data.remote.retrofit.ApiConfig
import com.aplikasi.mystory.data.local.StoryDatabase
import com.aplikasi.mystory.data.remote.repository.StoryRepository
import com.aplikasi.mystory.data.pref.UserPreference
import com.aplikasi.mystory.data.remote.repository.UserRepository
import com.aplikasi.mystory.data.pref.dataStore

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref,apiService)
    }
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(database, apiService, pref)
    }
}