package com.aplikasi.mystory.ui.detailStory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.aplikasi.mystory.data.remote.repository.StoryRepository

class DetailStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val storyId = MutableLiveData<String>()

    val detailStory = storyId.switchMap {
        storyRepository.getDetailStory(it)
    }

    fun getDetailStory(id: String) {
        storyId.value=id
    }
}
