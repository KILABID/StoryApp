package com.kilabid.storyapp.ui.MainPage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
        viewModel.listStory()

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

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter(emptyList())
        binding.rvStory.adapter = listAdapter
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        viewModel.listStory.observe(this) {
            listAdapter.submitList(it)
        }

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