package com.example.chatapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.databinding.LoginLayoutBinding
import com.example.chatapp.factory.LoginViewModelFactory
import com.example.chatapp.repository.LoginRepository
import com.example.chatapp.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var binding: LoginLayoutBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.login_layout)
        mAuth = FirebaseAuth.getInstance()
        progressBar = binding.loginProgressBar
        progressBar.visibility = View.GONE

        viewModel = ViewModelProvider(this, LoginViewModelFactory(LoginRepository()))[LoginViewModel::class.java]

        binding.loginTrueButton.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showErrorDialog("Please fill in all fields.")
            } else {
                progressBar.visibility = View.VISIBLE
                viewModel.signInWithEmailAndPassword(email, password){
                    progressBar.visibility = View.GONE
                    if (it){
                        val intent = Intent(this, UserListViewActivity::class.java)
                        startActivity(intent)
                    }else{

                    }
                }
            }
        }

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
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