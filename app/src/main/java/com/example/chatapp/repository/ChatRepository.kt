package com.example.chatapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.ChatData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatRepository {
    private val database = FirebaseDatabase.getInstance().reference

    fun getChatData(chatId: String): LiveData<ChatData> {
        val liveData = MutableLiveData<ChatData>()

        database.child("chatters").child(chatId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(ChatData::class.java)
                if (userData != null) {
                    liveData.value = userData!!
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve chat data", error.toException())
            }
        })

        return liveData
    }

    fun sendMessage(chatId: String, message: String) {
        database.child("chatters").child(chatId).push().setValue(message)
    }

    companion object {
        private const val TAG = "ChatRepository"
    }
}