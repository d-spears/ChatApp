package com.app.chatapp.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {
    private val userVM = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = userVM

    fun signInWithEmailAndPassword(email: String, password: String, callback:(Boolean) -> Unit) {
        repository.signInWithEmailAndPassword(email, password) { firebaseUser, throwable ->
            if (throwable != null) {
                userVM.value = null
                Log.w(ContentValues.TAG, "SignIn with email and password failed", throwable)
                callback (false)
            } else {
                userVM.value = firebaseUser
                callback(true)
            }
        }
    }
}