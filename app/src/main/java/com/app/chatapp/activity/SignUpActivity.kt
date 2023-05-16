package com.app.chatapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.databinding.SignUpLayoutBinding
import com.example.chatapp.factory.SignUpViewModelFactory
import com.example.chatapp.repository.SignUpRepository
import com.example.chatapp.viewmodel.SignUpViewModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: SignUpLayoutBinding
    private lateinit var viewModel: SignUpViewModel

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SignUpViewModelFactory(SignUpRepository()))[SignUpViewModel::class.java]

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
                viewModel.signUp(name, email, password, confirmPassword){
                    if (it == "success") {
                        finish()
                    } else {
                        showErrorDialog(it)
                    }
                }
                }
            }

        binding.loginFalseButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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