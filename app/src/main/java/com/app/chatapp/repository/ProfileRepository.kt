package com.app.chatapp.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.chatapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileRepository {
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("users")

    fun getUserList(): LiveData<List<User>> {
        val userListLiveData = MutableLiveData<List<User>>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { list.add(it) }
                }
                userListLiveData.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase Database Error: ${error.message}")
            }
        })
        return userListLiveData
    }
}