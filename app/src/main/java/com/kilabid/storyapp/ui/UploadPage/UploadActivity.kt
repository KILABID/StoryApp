package com.kilabid.storyapp.ui.UploadPage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kilabid.storyapp.databinding.ActivityUploadBinding
import com.kilabid.storyapp.di.ResultState
import com.kilabid.storyapp.ui.MainPage.MainActivity
import com.kilabid.storyapp.ui.UploadPage.UploadUtils.Companion.reduceFileImage
import com.kilabid.storyapp.ui.ViewModelFactory
import kotlinx.coroutines.launch

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    private var imageUri: Uri? = null

    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { gallery() }
        binding.cameraButton.setOnClickListener { camera() }
        binding.uploadButton.setOnClickListener { uploadImg() }
    }

    private fun gallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun camera() {
        imageUri = UploadUtils.getImageUri(this)
        launchCamera.launch(imageUri!!)
    }

    private fun uploadImg() {
        imageUri?.let { uri ->
            val fileImage = UploadUtils.uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImg: ${fileImage.path}")
            val description = binding.edAddDescription.text.toString()

            lifecycleScope.launch {
                viewModel.uploadImg(fileImage, description).collect { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            loading(true)
                        }

                        is ResultState.Success -> {
                            toast(result.data.message)
                            loading(false)

                            val intent = Intent(this@UploadActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)

                        }

                        is ResultState.Error -> {
                            toast(result.error)
                            loading(false)
                        }
                    }
                }
            }
        } ?: toast("Pilih gambar")
    }

    private fun prevImg() {
        imageUri?.let {
            Log.d("Image URI", "showImg: $it")
            binding.previewImage.setImageURI(it)
        }
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            prevImg()
        }
    }


    private fun loading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            prevImg()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

}