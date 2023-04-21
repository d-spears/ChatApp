package com.example.chatapp

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.databinding.LoginLayoutBinding
import com.example.chatapp.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    lateinit var binding: LoginLayoutBinding
    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.login_layout)
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("chatters")
        mAuth = FirebaseAuth.getInstance()
        progressBar = binding.loginProgressBar

        progressBar.visibility = View.GONE
        // Obtain an instance of the ViewModel
        val viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        // Set the ViewModel for the binding
        binding.viewModel = viewModel



        binding.loginTrueButton.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showErrorDialog("Please fill in all fields.")
            } else {
                progressBar.visibility = View.VISIBLE
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            val currentUser = mAuth.currentUser
                            startActivity(Intent(this, ChatListView::class.java))

                        } else {
                            val exception = task.exception
                            when {
                                exception is FirebaseAuthInvalidUserException -> {
                                    showErrorDialog("User not found.")
                                }
                                exception is FirebaseAuthInvalidCredentialsException -> {
                                    showErrorDialog("Email or password is incorrect.")
                                }
                                else -> {
                                    Log.w(ContentValues.TAG, "signInWithEmail:failure", exception)
                                }
                            }
                        }
                    }
            }
        }

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun showErrorDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id -> dialog.dismiss() }
        val alert = dialogBuilder.create()
        alert.setTitle("Error")
        alert.show()
    }
}