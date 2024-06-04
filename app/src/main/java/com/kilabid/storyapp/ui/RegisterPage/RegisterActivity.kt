package com.kilabid.storyapp.ui.RegisterPage

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kilabid.storyapp.R
import com.kilabid.storyapp.databinding.ActivityRegisterBinding
import com.kilabid.storyapp.di.ResultState
import com.kilabid.storyapp.ui.MainPage.MainActivity
import com.kilabid.storyapp.ui.ViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        actionListener()
        playAnimation()

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

    private fun actionListener() {
        binding.registerBtn.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            var valid = true
            when {
                name.isEmpty() -> binding.edRegisterName.setErrorIfEmpty(getString(R.string.field_empty))
                    .also { valid = false }

                email.isEmpty() -> binding.edRegisterEmail.setErrorIfEmpty(getString(R.string.field_empty))
                    .also { valid = false }

                password.isEmpty() -> binding.edRegisterPassword.setErrorIfEmpty(getString(R.string.field_empty))
                    .also { valid = false }
            }

            if (valid) {
                lifecycleScope.launch {
                    viewModel.userRegister(name, email, password).collect { result ->
                        when (result) {
                            is ResultState.Loading -> {
                                Log.d("Loading", "Loading")
                                showLoading(true)
                            }

                            is ResultState.Success -> {
                                result.data.message?.let { it1 -> showToast(it1) }
                                showLoading(false)
                                showDialog()
                            }

                            is ResultState.Error -> {
                                showToast(result.error)
                                showLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun EditText.setErrorIfEmpty(message: String) {
        if (text.isEmpty()) {
            error = message
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Successed")
            setMessage("Register Berhasil")
            setPositiveButton("Next") { _, _ ->
                val mainIntent = Intent(context, MainActivity::class.java)
                mainIntent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(mainIntent)
                finish()
            }
            create()
            show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.registerPhoto, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.registerBtn, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                name,
                email,
                password,
                register
            )
            startDelay = 100
        }.start()
    }
}