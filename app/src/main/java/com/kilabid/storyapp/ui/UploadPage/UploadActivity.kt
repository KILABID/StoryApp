package com.kilabid.storyapp.ui.UploadPage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
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

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.galleryButton.setOnClickListener { gallery() }
        binding.cameraButton.setOnClickListener { camera() }
        binding.switchLocation.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val locLat = location.latitude
                            val locLon = location.longitude
                            toast("Location $locLat, $locLon")
                            binding.uploadButton.setOnClickListener { uploadImg(locLat, locLon) }
                        }
                    }
                } else {
                    toast("Permission Denied")
                }
            } else {
                toast("Upload Image Without Location")
                binding.uploadButton.setOnClickListener { uploadImg(0.0, 0.0) }
            }
        }
    }

    private fun gallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun camera() {
        imageUri = UploadUtils.getImageUri(this)
        launchCamera.launch(imageUri!!)
    }

    private fun uploadImg(lat: Double, lon: Double) {
        imageUri?.let { uri ->
            val fileImage = UploadUtils.uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImg: ${fileImage.path}")
            val description = binding.edAddDescription.text.toString()
            val latLng = LatLng(lat, lon)

            lifecycleScope.launch {
                viewModel.uploadImg(fileImage, description, lat, lon).collect { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            loading(true)
                        }

                        is ResultState.Success -> {
                            toast(result.data.message)
                            loading(false)
                            if (latLng != LatLng(0.0,0.0)) {
                                toast("Data Send With Location")
                            } else {
                                toast("Data Send Without Location")
                            }

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