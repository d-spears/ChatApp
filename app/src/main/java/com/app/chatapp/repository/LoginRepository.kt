package com.app.chatapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginRepository {

    private val mAuth = FirebaseAuth.getInstance()

    fun signInWithEmailAndPassword(email: String, password: String, callback: (FirebaseUser?, Throwable?) -> Unit) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(mAuth.currentUser, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }
}