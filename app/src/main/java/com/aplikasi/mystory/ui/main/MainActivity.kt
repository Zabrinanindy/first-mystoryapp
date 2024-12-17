package com.aplikasi.mystory.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplikasi.mystory.R
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.databinding.ActivityMainBinding
import com.aplikasi.mystory.ui.WelcomeActivity
import com.aplikasi.mystory.ui.ViewModelFactory
import com.aplikasi.mystory.ui.adapter.StoryAdapter
import com.aplikasi.mystory.ui.addStory.AddStoryActivity
import com.aplikasi.mystory.ui.detailStory.DetailStoryActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val storyAdapter = StoryAdapter()

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        setupView()
        setupViewModel()
        setupClickListeners()

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.root.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> showLogoutDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Apakah kamu yakin ingin Keluar ?")
            setPositiveButton("Ya") { _, _ ->
                viewModel.logout()
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            setNegativeButton("Tidak", null)
            create()
            show()
        }
    }

    private fun setupView() {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }

    private fun setupViewModel() {
        viewModel.story.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                    binding.root.isRefreshing = false
                }
                is ResultState.Success -> {
                    showLoading(false)
                    val stories = result.data
                    if (stories.isNotEmpty()) {
                        storyAdapter.setStories(stories)
                    }
                }
                is ResultState.Error -> {
                    showLoading(false)
                    binding.root.isRefreshing = false
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        storyAdapter.setOnItemCallback { story ->
            val intent = Intent(this, DetailStoryActivity::class.java)
            intent.putExtra(DetailStoryActivity.EXTRA_ID, story.id)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvStory.isVisible = !isLoading
    }
}
