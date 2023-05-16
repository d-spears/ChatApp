package com.app.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.model.User
import com.example.chatapp.repository.UserRepository

class UserListViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getUsers(loggedInUserId: String): LiveData<List<User>> {
        return userRepository.getUsers(loggedInUserId)
    }
}
