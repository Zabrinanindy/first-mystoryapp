package com.aplikasi.mystory.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.data.local.StoryDatabase
import com.aplikasi.mystory.data.pref.UserPreference
import com.aplikasi.mystory.data.remote.response.AddStoryResponse
import com.aplikasi.mystory.data.remote.response.ListStoryItem
import com.aplikasi.mystory.data.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private val resultStories = MediatorLiveData<ResultState<List<ListStoryItem>>>()

    fun getAllStory(): LiveData<ResultState<List<ListStoryItem>>> {
        resultStories.value = ResultState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = userPreference.getToken().first()
                val response = apiService.getAllStory("Bearer $token")
                val storyList = response.listStory?.mapNotNull { it } ?: emptyList()

                storyDatabase.storyDao().deleteAll()
                storyDatabase.storyDao().insertStory(storyList)

                withContext(Dispatchers.Main) {
                    resultStories.addSource(storyDatabase.storyDao().getAllStory()) { newData ->
                        resultStories.value = ResultState.Success(newData)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resultStories.value = ResultState.Error("Error fetching stories: ${e.message}")
                }
            }
        }
        return resultStories
    }

    fun getDetailStory(id: String): LiveData<ResultState<ListStoryItem>> = liveData {
        emit(ResultState.Loading)
        try {
            val token = userPreference.getToken().first()
            val response = apiService.detailStory("Bearer $token", id)
            val result = response.story
            emit(ResultState.Success(result))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun addStory(image: File, description: String): LiveData<ResultState<AddStoryResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val token = userPreference.getToken().first()
            val requestDescription = description.toRequestBody("text/plain".toMediaType())
            val requestImage = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                image.name,
                requestImage
            )
            val response = apiService.addStory("Bearer $token", imageMultipart, requestDescription)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService, userPreference)
            }.also { instance = it }
    }
}
