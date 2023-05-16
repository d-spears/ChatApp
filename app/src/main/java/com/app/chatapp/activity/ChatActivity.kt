package com.app.chatapp.activity


import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.adapter.MessageAdapter
import com.example.chatapp.databinding.ChatPageLayoutBinding
import com.example.chatapp.model.MessageData
import com.example.chatapp.repository.ChatRepository
import com.example.chatapp.viewmodel.ChatViewModel
import com.example.chatapp.factory.ChatViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {
    private lateinit var bind: ChatPageLayoutBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var toolbar: Toolbar
    private lateinit var profileImageView: ImageView
    private lateinit var receiverId: String
    lateinit var userName: String
    lateinit var firebaseToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ChatPageLayoutBinding.inflate(layoutInflater)
        setContentView(bind.root)
        toolbar = findViewById(R.id.chat_page_toolbar)
        profileImageView = toolbar.findViewById(R.id.toolbar_image_view)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        userName = intent.getStringExtra("userName").toString()
        receiverId = intent.getStringExtra("receiverId").toString()
        firebaseToken = intent.getStringExtra("firebaseToken").toString()
        supportActionBar?.title = userName

        val userImageUrl = intent.getStringExtra("image")
        Glide.with(this)
            .load(userImageUrl)
            .placeholder(R.drawable.user)
            .circleCrop()
            .into(profileImageView)

        firestoreDb = FirebaseFirestore.getInstance()

        // Set up RecyclerView and adapter
        bind.messageList.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = MessageAdapter(mutableListOf())
        }

        // Set up listener for new messages
        val chatRepository = ChatRepository()
        chatViewModel = ViewModelProvider(this, ChatViewModelFactory(chatRepository))[ChatViewModel::class.java]

        chatViewModel.getMessageData(FirebaseAuth.getInstance().currentUser?.uid ?: "", receiverId).observe(this) { messageDataList ->
            if (messageDataList != null) {
                bind.messageList.adapter?.let { adapter ->
                    if (adapter is MessageAdapter) {
                        messageDataList.forEach { messageData ->
                            adapter.addMessage(messageData)
                        }
                    }
                }
            }
        }

        bind.sendButton.setOnClickListener {
            val messageText = bind.messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                sendMessageData(messageText, senderId, receiverId)
                bind.messageInput.text.clear()
            }
        }
    }
    private fun sendMessageData(messageText: String, senderId: String, receiverId: String) {
        val message = MessageData(senderId, receiverId, messageText)
        chatViewModel.sendMessageData(message, userName, firebaseToken)
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
    companion object {
        private const val TAG = "ChatActivity"
    }
}