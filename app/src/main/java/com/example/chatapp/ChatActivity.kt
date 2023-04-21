package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.adapter.MessageAdapter
import com.example.chatapp.databinding.ChatPageLayoutBinding
import com.example.chatapp.model.MessageData
import com.example.chatapp.viewmodel.ChatViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ChatPageLayoutBinding
    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<MessageData>()
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var messageText: TextView
    private lateinit var chatRoomId: String
    private lateinit var toolbar: Toolbar
    private lateinit var menuIcon: ImageView

    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatPageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = findViewById(R.id.chat_list_toolbar)

        val recyclerView = binding.messageList
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = MessageAdapter(messages)
        recyclerView.adapter = adapter

        messageText = binding.messageInput

        // Action Bar
        setSupportActionBar(toolbar)
        val userName = intent.getStringExtra("userName")
        supportActionBar?.title = "Chat with $userName"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true) // title in the toolbar

        val messagesRef = firebaseFirestore.collection("chat-messages")

        chatRoomId = intent.getStringExtra("chatRoomId")?.let { it } ?: ""

        chatViewModel.getChatData(chatRoomId).observe(this) { chatData ->

        }

        val loadedMessages = loadMessages() // Load the messages from SharedPreferences
        messages.addAll(loadedMessages)
        adapter.updateList(messages)
        adapter.notifyDataSetChanged()

        binding.sendButton.setOnClickListener {
            val messageTextShow = binding.messageInput.text.toString()
            if (messageTextShow.isNotBlank()) {
                val message = MessageData(
                    senderId = "currentUserId",
                    receiverId = "receiverId",
                    messageText = messageTextShow
                )
                messagesRef.add(message)
                    .addOnSuccessListener { documentReference ->
                        Log.d("ChatActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("ChatActivity", "Error adding document", e)
                    }
                //binding.messageInput.text.clear()
                adapter.addMessage(message) // Add the message to the adapter's list and notify the adapter
                binding.messageList.scrollToPosition(adapter.itemCount - 1) // Scroll to the last message
            }
        }

        messagesRef.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("ChatActivity", "Error getting messages", error)
                return@addSnapshotListener
            }

            val newMessages = mutableListOf<MessageData>()
            for (document in value!!) {
                val message = document.toObject(MessageData::class.java)
                newMessages.add(message)
            }
            adapter.updateList(newMessages)
            adapter.notifyDataSetChanged()
            messages.addAll(newMessages)
            saveMessages(messages) // Save the messages in SharedPreferences
        }

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
                val intent = Intent(this@ChatActivity, ChatListView::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadMessages(): List<MessageData> {
        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val json = sharedPrefs.getString("messages", null)
        return if (json != null) Gson().fromJson(json, object : TypeToken<List<MessageData>>() {}.type) else emptyList()
    }

    private fun saveMessages(messages: List<MessageData>) {
        val json = Gson().toJson(messages)
        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("messages", json).apply()
    }

}