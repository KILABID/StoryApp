package com.kilabid.storyapp.ui.DetailPage

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kilabid.storyapp.data.remote.response.Story
import com.kilabid.storyapp.databinding.ActivityDetailBinding
import com.kilabid.storyapp.di.ResultState
import com.kilabid.storyapp.ui.ViewModelFactory
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("EXTRA_ID")
        supportActionBar?.hide()

        lifecycleScope.launch {
            if (userId != null) {
                viewModel.getDetailStories(userId).collect { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            showLoading(false)
                            result.data.story.let { bindDetailStory(it) }
                        }

                        is ResultState.Error -> {
                            showLoading(false)
                            result.error
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun bindDetailStory(user: Story) {
        binding.tvDetailName.text = user.name
        binding.tvDetailDescription.text = user.description
        Glide.with(binding.root.context)
            .load(user.photoUrl)
            .into(binding.ivDetailPhoto)
    }

}