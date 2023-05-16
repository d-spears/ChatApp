package com.app.chatapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.User
import com.google.firebase.database.*

class UserRepository {
    private val mFirebaseDatabase: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("Users")
    }

    fun getUsers(loggedInUserId: String): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()

        mFirebaseDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()

                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)

                    if (user != null && user.userID != loggedInUserId) {
                        userList.add(user)
                    }
                }

                liveData.value = userList
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return liveData
    }
}