package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.SignUpLayoutBinding
import com.example.chatapp.model.ChatData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding: SignUpLayoutBinding
    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("chatters")
        mAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val name = binding.loginName.text.toString().trim()
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()
            val confirmPassword = binding.loginConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showErrorDialog("Please fill in all fields.")
            } else if (password != confirmPassword) {
                showErrorDialog("Passwords do not match.")
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val userId = authResult.user?.uid
                        val user = ChatData(userId!!, name, email, "")

                        mFirebaseDatabase.child(userId).setValue(user)
                            .addOnSuccessListener {
                                //startActivity(Intent(this@SignUp, ChatListView::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                // Handle error
                            }
                    }
                    .addOnFailureListener {
                        // Handle error
                    }
            }
        }

        binding.loginFalseButton.setOnClickListener {
            startActivity(Intent(this@SignUp, Login::class.java))
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        AlertDialog.Builder(this)
            .setMessage(errorMessage)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setTitle("Error")
            .create()
            .show()
    }
}