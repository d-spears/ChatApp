package com.example.chatapp

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
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.adapter.Adapter
import com.example.chatapp.databinding.ChatListLayoutBinding
import com.example.chatapp.model.ChatData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.bumptech.glide.Glide

class ChatListView : AppCompatActivity() {
    private lateinit var binding: ChatListLayoutBinding
    private var userList: HashMap<String, ChatData> = hashMapOf()
    private var mFirebaseDatabase: DatabaseReference? = null
    private lateinit var mChatRoomId: String
    private var imageUri: Uri? = null
    private var menuIcon: ImageView? = null
    private lateinit var toolbar: Toolbar
    private var actionView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.chat_toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("chatters")
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        val adapter = Adapter(this, userList)
        binding.recyclerViewShow.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewShow.adapter = adapter

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        mFirebaseDatabase?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val person = snapshot.getValue(ChatData::class.java)
                    if (person?.chatID != currentUserId) {
                        person?.name?.let { userList[it] = person }
                    }
                }
                adapter.setData(userList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu?.findItem(R.id.menu_item_one)
        val actionView = MenuItemCompat.getActionView(menuItem!!)
        menuIcon = actionView.findViewById(R.id.menu_item_icon)
        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val imageUri = sharedPrefs.getString("image_uri", null)
        if (menuIcon != null && imageUri != null) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .circleCrop()
                .into(menuIcon!!)
        }

        menuIcon?.setOnClickListener {
            val imageIntent = Intent(this, ProfileActivity::class.java)
            startActivity(imageIntent)
        }

        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val backIntent = Intent(this@ChatListView, Login::class.java)
                startActivity(backIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}