package com.example.chatapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.activity.CameraActivity
import com.example.chatapp.model.User
import com.example.chatapp.repository.ProfileRepository
import com.google.firebase.database.*
import java.lang.Exception

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    fun getUserList(): LiveData<List<User>> {
        return repository.getUserList()
    }
}