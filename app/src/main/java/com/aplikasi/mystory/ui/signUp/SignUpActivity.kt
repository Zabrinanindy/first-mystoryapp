package com.aplikasi.mystory.ui.signUp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aplikasi.mystory.data.ResultState
import com.aplikasi.mystory.ui.ViewModelFactory
import com.aplikasi.mystory.databinding.ActivitySignUpBinding
import com.aplikasi.mystory.ui.login.LoginActivity


class SignUpActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SignUpActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupView()
        setupAction()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivSignup, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.textSignup, View.ALPHA, 1f).setDuration(100)
        val usernameEdit =
            ObjectAnimator.ofFloat(binding.edtUsername, View.ALPHA, 1f).setDuration(100)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(100)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(100)
        val signUp = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(100)
        val textLogin = ObjectAnimator.ofFloat(binding.textLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                usernameEdit,
                emailEdit,
                passwordEdit,
                signUp,
                textLogin
            )
            startDelay = 100
        }.start()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            LoginActivity.start(this@SignUpActivity)
        }
        binding.btnSignup.setOnClickListener {

            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (!isValid(username, email, password)) {
                showMessage("Wajib diisi")
                return@setOnClickListener
            }

            viewModel.signUp(username, email, password).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> { showLoading(true) }
                    is ResultState.Success -> {
                        showLoading(false)
                        result.data.message?.let {
                            showMessage(it)
                        } ?: showError("Unknown error")
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        showError(result.error)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isValid(username: String, email: String, password: String): Boolean {
        val isUsernameValid = username.isNotEmpty()
        val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.isNotEmpty() && password.length >= 8

        return isUsernameValid && isEmailValid && isPasswordValid
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}