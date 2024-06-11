package com.kilabid.storyapp.ui.MainPage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kilabid.storyapp.R
import com.kilabid.storyapp.databinding.ActivityMainBinding
import com.kilabid.storyapp.ui.LandingPage.LandingActivity
import com.kilabid.storyapp.ui.UploadPage.UploadActivity
import com.kilabid.storyapp.ui.ViewModelFactory
import com.kilabid.storyapp.ui.mapsPage.MapsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listAdapter: ListAdapter
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            Log.d("token", user.token)
            if (!user.isLogin) {
                val intent = Intent(this, LandingActivity::class.java)
                Toast.makeText(this, "Logout Berhasil", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }
        }

        binding.uploadButton.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
        setupView()
        setupRecyclerView()
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
        supportActionBar
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showDialog()
                true
            }
            R.id.action_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = listAdapter.withLoadStateFooter(
            footer = RefreshAdapter {
                listAdapter.retry()
            }
        )

        showLoading(true)
        viewModel.story.observe(this) { pagingData ->
            listAdapter.submitData(lifecycle, pagingData)
            showLoading(false)
        }

    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Alert")
            setMessage("Apakah anda yakin ingin keluar?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.logout()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}