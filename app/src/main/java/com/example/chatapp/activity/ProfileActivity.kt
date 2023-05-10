package com.example.chatapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ProfileLayoutBinding
import com.example.chatapp.viewmodel.ProfileViewModel
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding: ProfileLayoutBinding
    private lateinit var profileLayout: ConstraintLayout
    private lateinit var profileIcon: AppCompatImageView
    private lateinit var takeSelfieButton: Button // camera
    private lateinit var choosePhotoButton: Button // photo
    private lateinit var logoutButton: Button
    private lateinit var saveButton: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // toolbar
        toolbar = findViewById(R.id.profile_toolbar)
        setSupportActionBar(toolbar)
        saveButton = toolbar.findViewById(R.id.toolbar_save_icon)
        saveButton.setOnClickListener {
            saveImage()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // arrow in toolbar
        supportActionBar?.setDisplayShowTitleEnabled(true) // title in the toolbar
        supportActionBar?.title = "Profile"

        profileLayout = binding.profileLayout
        takeSelfieButton = binding.selfieButton
        choosePhotoButton = binding.chooseImageButton
        logoutButton = binding.logoutButton
        profileIcon = binding.profileImageView

        // view model
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profileViewModel.profileImage.observe(this) {
            if (it != null) {
                loadImage(it)
            }
        }

        // buttons
        choosePhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }

        takeSelfieButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, CameraActivity::class.java)
            startActivity(intent)
        }

        profileViewModel.loadSavedProfileImage()

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null && data.data != null) {
                    imageUri = data.data
                    val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putString("image_uri", imageUri.toString())
                    editor.apply()
                    profileViewModel.loadSavedProfileImage()
                }
            }
        }

        logoutButton.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
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

    private fun loadImage(imageUri: Uri) {
        Glide.with(this)
            .load(imageUri)
            .into(profileIcon)
    }

    private fun saveImage() {
        profileViewModel.saveImage(imageUri, applicationContext)
    }
}