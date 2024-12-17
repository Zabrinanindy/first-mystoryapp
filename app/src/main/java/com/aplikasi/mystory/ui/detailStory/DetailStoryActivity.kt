package com.aplikasi.mystory.ui.detailStory

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.data.remote.response.ListStoryItem
import com.aplikasi.mystory.databinding.ActivityDetailStoryBinding
import com.aplikasi.mystory.ui.ViewModelFactory
import com.aplikasi.mystory.util.withDateFormat
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_ID)
        if (storyId != null) {
            viewModel.getDetailStory(storyId)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.detailStory.observe(this) { result ->
            when (result) {
                is ResultState.Error -> {
                    Log.e("DetailStoryActivity", "Error: ${result.error}")
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
                is ResultState.Loading -> {
                    Log.d("DetailStoryActivity", "Loading...")
                    showLoading(true)
                }
                is ResultState.Success -> {
                    Log.d("DetailStoryActivity", "Data: ${result.data}")
                    showLoading(false)
                    displayStoryDetail(result.data)
                }
            }
        }

    }

    private fun displayStoryDetail(story: ListStoryItem) {
        Log.d("DetailStoryActivity", "Displaying story: $story")
        with(binding) {
            Glide.with(this@DetailStoryActivity)
                .load(story.photoUrl)
                .into(imageStory)
            userName.text = story.name
            dateStory.text = story.createdAt?.withDateFormat()
            descStory.text = story.description
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ID = "id"
    }
}
