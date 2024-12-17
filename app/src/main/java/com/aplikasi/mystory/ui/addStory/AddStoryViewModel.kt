package com.aplikasi.mystory.ui.addStory

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.data.remote.repository.StoryRepository
import com.aplikasi.mystory.data.remote.response.AddStoryResponse
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun uploadImage(image: File, description: String): LiveData<ResultState<AddStoryResponse>> {
        return storyRepository.addStory(image, description)
    }

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }
}