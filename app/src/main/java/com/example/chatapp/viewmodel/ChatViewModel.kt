package com.example.chatapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.PushNotification
import com.example.chatapp.model.MessageData
import com.example.chatapp.model.NotificationData
import com.example.chatapp.repository.ChatRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("Messages")
    lateinit var topic: String

    fun getMessageData(senderID: String, receiverID: String): LiveData<MessageData> {
        val messageData = MutableLiveData<MessageData>()
        database.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(dataSnapshot: DataSnapshot in snapshot.children){
                        val messageSend = dataSnapshot.getValue(MessageData::class.java)
                        if (messageSend != null) {
                            if( (messageSend.senderId == senderID && messageSend.receiverId == receiverID) ||
                                messageSend.senderId == receiverID && messageSend.receiverId == senderID){
                                messageData.value = messageSend!!
                            }
                        } else {
                            Log.e(TAG, "Error: messageSend is null")
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        return messageData
    }

    fun sendMessageData(message: MessageData, userName: String, firebaseToken: String) {
        repository.sendMessage(message)
        topic = "/topics/${firebaseToken}"
        PushNotification(NotificationData(userName, message.messageText), firebaseToken).also {
            sendNotification(it)
        }
    }

    constructor() : this(ChatRepository()) {}

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if(response.isSuccessful) {
                   // Log.d("TAG", "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e("TAG", response.errorBody()!!.string())
                }
            } catch(e: Exception) {
                Log.e("TAG", e.toString())
            }

    }

    companion object {
        private const val TAG = "ChatViewModel"
    }
}