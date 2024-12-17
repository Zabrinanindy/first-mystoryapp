package com.aplikasi.mystory.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aplikasi.mystory.data.remote.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<ListStoryItem>)

    @Query("SELECT * FROM story")
    fun getAllStory(): LiveData<List<ListStoryItem>>


    @Query("DELETE FROM story")
    suspend fun deleteAll()
}