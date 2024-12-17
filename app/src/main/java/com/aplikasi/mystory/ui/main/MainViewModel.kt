package com.aplikasi.mystory.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.data.pref.UserModel
import com.aplikasi.mystory.data.remote.repository.StoryRepository
import com.aplikasi.mystory.data.remote.repository.UserRepository
import com.aplikasi.mystory.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(
    private val storyRepository: StoryRepository,
    private val repository: UserRepository
) : ViewModel() {

    val story: LiveData<ResultState<List<ListStoryItem>>> = storyRepository.getAllStory()

    private val _isLoading = MutableLiveData<Boolean>()

    private val _errorMessage = MutableLiveData<String?>()

    fun refreshData() {
        storyRepository.getAllStory().observeForever { result ->
            when (result) {
                is ResultState.Loading -> {
                    _isLoading.value = true
                    _errorMessage.value = null
                }
                is ResultState.Success -> {
                    _isLoading.value = false
                    _errorMessage.value = null
                }
                is ResultState.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.error
                }
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
