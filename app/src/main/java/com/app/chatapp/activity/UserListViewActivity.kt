package com.app.chatapp.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.adapter.Adapter
import com.example.chatapp.databinding.UserRecyclerLayoutBinding
import com.example.chatapp.factory.UserListViewModelFactory
import com.example.chatapp.model.User
import com.example.chatapp.repository.UserRepository
import com.example.chatapp.viewmodel.UserListViewModel
import com.google.firebase.auth.FirebaseAuth

class UserListViewActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter
    private lateinit var viewModel: UserListViewModel
    private lateinit var bind: UserRecyclerLayoutBinding
    private lateinit var toolbar: Toolbar
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = UserRecyclerLayoutBinding.inflate(layoutInflater)
        setContentView(bind.root)
        toolbar = findViewById(R.id.user_toolbar)

        setSupportActionBar(toolbar)
        toolbar.hideOverflowMenu()
        supportActionBar?.title = "User List"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // arrow in toolbar
        supportActionBar?.setDisplayShowTitleEnabled(true) // title in the toolbar

        profileImageView = toolbar.findViewById(R.id.toolbar_image_view) // initialize profileImageView
        profileImageView.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        recyclerView = bind.recyclerViewShow
        adapter = Adapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        viewModel = ViewModelProvider(this, UserListViewModelFactory(UserRepository()))[UserListViewModel::class.java]

        viewModel.getUsers(loggedInUserId).observe(this) { userList ->
            val users = HashMap<String, User>()
            for (user in userList) {
                users[user.userID.toString()] = user
            }
            adapter.setData(users, loggedInUserId)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu ?: return super.onPrepareOptionsMenu(menu)
        // Load the saved image URI from shared preferences
        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val imageUri = sharedPrefs.getString("image_uri", null)

        // If the URI exists, load the image into the toolbar image view
        if (imageUri != null && profileImageView != null) {
            val savedIconUri = Uri.parse(imageUri)
            profileImageView.visibility = View.VISIBLE // make the image view visible
            Glide.with(this)
                .load(savedIconUri)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(profileImageView)

            // Set an OnClickListener to navigate to the profile activity
            profileImageView.setOnClickListener {
                val intent = Intent(this@UserListViewActivity, ProfileActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@UserListViewActivity, LoginActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}