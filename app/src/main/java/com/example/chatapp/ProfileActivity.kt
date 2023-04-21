package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.ImageUploadBinding
import com.example.chatapp.model.ChatData
import com.google.firebase.database.*
class ProfileActivity : AppCompatActivity() {
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private lateinit var binding: ImageUploadBinding
    private var imageUri: Uri? = null
    private var userList = arrayListOf<ChatData>()
    private lateinit var profileLayout: ConstraintLayout
    private lateinit var profileIcon: AppCompatImageView
    private lateinit var takeSelfieButton: Button // camera
    private lateinit var choosePhotoButton: Button // photo
    private lateinit var toolbar: Toolbar
    private lateinit var menuIcon: ImageView
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImageUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.profile_toolbar)

        setSupportActionBar(toolbar)
        //toolbar.title = "Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // arrow in toolbar
        supportActionBar?.setDisplayShowTitleEnabled(true) // title in the toolbar
        supportActionBar?.title = "Profile"

        profileLayout = binding.profileLayout
        takeSelfieButton = binding.selfieButton
        choosePhotoButton = binding.chooseImageButton
        profileIcon = binding.profileImageView

        choosePhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        takeSelfieButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, ImagePreview::class.java)
            startActivity(intent)
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
    //both profile icon and menu icon
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu ?: return super.onPrepareOptionsMenu(menu)
        val menuItem = menu.findItem(R.id.menu_item_one)
        val actionView = menuItem?.actionView
        menuIcon = (actionView?.findViewById(R.id.menu_item_icon) as? ImageView)!!
        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val imageUri = sharedPrefs.getString("image_uri", null)
        if (imageUri != null && menuIcon != null) {
            val savedIconUri = Uri.parse(imageUri)
            menuIcon.visibility = View.VISIBLE
            Glide.with(this)
                .load(savedIconUri)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(profileIcon)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    // toolbar?
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null)
            imageUri = data.data
        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("image_uri", imageUri.toString())
        editor.apply()
        menuIcon?.visibility = View.VISIBLE
        Glide.with(this)
                .load(sharedPrefs.getString("image_uri", null))
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(profileIcon)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@ProfileActivity, ChatListView::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}