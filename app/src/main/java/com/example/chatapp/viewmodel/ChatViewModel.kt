package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.model.ChatData
import com.example.chatapp.repository.ChatRepository

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()


    var email = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var password = MutableLiveData<String>()

    fun getChatData(chatId: String): LiveData<ChatData> {
        return repository.getChatData(chatId)
    }

    fun sendMessage(chatId: String, message: String) {
        repository.sendMessage(chatId, message)
    }
}