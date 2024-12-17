package com.aplikasi.mystory.ui.addStory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aplikasi.mystory.R
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.databinding.ActivityAddStoryBinding
import com.aplikasi.mystory.ui.ViewModelFactory
import com.aplikasi.mystory.ui.main.MainActivity
import com.aplikasi.mystory.util.getImageUri
import com.aplikasi.mystory.util.reduceFileImage
import com.aplikasi.mystory.util.uriToFile
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private var getFile: File? = null
    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission granted")
            } else {
                showToast("Permission denied")
            }
        }

    private val launcherTakePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentImageUri?.let { uri ->
                    binding.ivAdd.setImageURI(uri)
                    binding.ivAdd.setPadding(0, 0, 0, 0)
                    getFile = uriToFile(uri, this)
                }
            } else {
                showToast(getString(R.string.empty_image))
                currentImageUri = null
            }
        }

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                viewModel.setImageUri(it)
                binding.ivAdd.setImageURI(it)
                binding.ivAdd.setPadding(0, 0, 0, 0)
            } ?: showToast(getString(R.string.empty_image))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupPermission()
        setupAction()

        viewModel.imageUri.observe(this) { uri ->
            uri?.let { getFile = uriToFile(it, this) }
        }
    }

    private fun setupPermission() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun setupAction() {
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnUpload.setOnClickListener {
            binding.edtDescription.clearFocus()
            uploadImage()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherTakePicture.launch(currentImageUri!!)
    }

    private fun uploadImage() {
        val description = binding.edtDescription.text.toString().trim()

        if (getFile == null) {
            showToast(getString(R.string.empty_image))
        } else if (description.isEmpty()) {
            binding.edtDescription.error = getString(R.string.empty_desc)
            binding.edtDescription.requestFocus()
        } else {
            val imageFile = getFile?.reduceFileImage()
            viewModel.uploadImage(imageFile!!, description).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        showLoading(false)
                        result.data.message?.let { showToast(it) }
                        val intent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                    is ResultState.Error -> {
                        showError(result.error)
                        showLoading(false)
                    }
                }
            }
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
