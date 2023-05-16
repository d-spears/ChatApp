package com.app.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chatapp.repository.SignUpRepository

class SignUpViewModel(private val repository: SignUpRepository) : ViewModel() {
    fun signUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String, callback:(String) -> Unit) {
        val signUpResult = repository.signUp(name, email, password, confirmPassword){
            callback(it)
        }
    }
}