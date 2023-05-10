package com.example.chatapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapter.Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class UserListViewActivity : AppCompatActivity() {

    private var userList: HashMap<String, User> = hashMapOf()
    private var mFirebaseDatabase: DatabaseReference? = null
    private lateinit var toolbar: Toolbar
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.user_recycler_layout)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("Token", token)
            val prefs = getSharedPreferences("sharedPref", MODE_PRIVATE)
            val prefsEditor = prefs.edit()
            prefsEditor.putString("token", token)
            prefsEditor.apply()
            //save token here
            val databaseRef = FirebaseDatabase.getInstance().getReference("Users")
            val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
            if(currentUserID != null){
                databaseRef.child(currentUserID).child("firebaseToken").setValue(token)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Firebase Token Saved", Toast.LENGTH_LONG).show()
                    }
            }
        })

        toolbar = findViewById(R.id.user_toolbar)
        profileImageView = toolbar.findViewById(R.id.toolbar_image_view)

        profileImageView.setOnClickListener {
            navigateToProfilePage()
        }
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Users List"

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("Users")
        initializeRecyclerView()
    }

    private fun navigateToProfilePage() {
        val intent = Intent(this@UserListViewActivity, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun initializeRecyclerView() {
        val adapter = Adapter(this, userList)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_show)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        mFirebaseDatabase?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val person = snapshot.getValue(User::class.java)
                    if (person?.userID != currentUserId) {
                        person?.name?.let { userList[it] = person }
                    }
                    else{
                            Glide.with(this@UserListViewActivity)
                                .load(Uri.parse(person?.image))
                                .circleCrop()
                                .placeholder(R.drawable.user)
                                .into(profileImageView)
                    }
                }
                adapter.setData(userList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}