package com.example.chatapp.repository

import com.example.chatapp.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class SignUpRepository {
    private val mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("Users")
    private val mAuth = FirebaseAuth.getInstance()

    fun signUp(name: String, email: String, password: String, confirmPassword: String, callback: (String) -> Unit){
                mAuth.createUserWithEmailAndPassword(email, password).onSuccessTask { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    val user = User(userId, name, email, "")

                    mFirebaseDatabase.child(userId).setValue(user).continueWith { task ->
                        if (task.isSuccessful) {
                             callback ("success")
                        } else {
                            callback(task.exception?.message.toString())
                        }
                    }
                } else {
                    callback("An error occurred while signing up.")
                    throw Exception("An error occurred while signing up.")
                }
            }
        }
    }