package com.app.chatapp.repository

import com.example.chatapp.model.MessageData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class ChatRepository {
    private val database = FirebaseDatabase.getInstance().getReference("Messages")

    fun getMessage(messageId: String): Task<DataSnapshot> {
        return database.child(messageId).get()
    }
    fun sendMessage(message: MessageData) {
        database.push().setValue(message)
    }
}