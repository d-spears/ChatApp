package com.app.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.chatapp.model.User
import com.app.chatapp.repository.ProfileRepository

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    fun getUserList(): LiveData<List<User>> {
        return repository.getUserList()
    }
}