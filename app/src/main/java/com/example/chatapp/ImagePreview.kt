package com.example.chatapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.ImageLayoutBinding
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ImagePreview : AppCompatActivity() {

    private lateinit var bind: ImageLayoutBinding
    private lateinit var selfieImage: PreviewView
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private lateinit var imageCapture: ImageCapture
    private lateinit var imgCaptureExecutor: ExecutorService
    private var menuIcon: ImageView? = null
    private var imageUri: Uri? = null
    private lateinit var outputDirectory: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ImageLayoutBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val toolbar: Toolbar = findViewById(R.id.image_toolbar)

        setSupportActionBar(toolbar)

        selfieImage = findViewById(R.id.selfie_camera_preview)

        supportActionBar?.title = "Choose A New Image"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // arrow in toolbar
        supportActionBar?.setDisplayShowTitleEnabled(true) // title in the toolbar

        outputDirectory = getOutputDirectory()

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
        val cameraPermissionResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                if (permissionGranted) {
                    startCamera()
                } else {
                    Snackbar.make(
                        toolbar,
                        "The Camera permission is required for running this application",
                        Snackbar.LENGTH_INDEFINITE
                    ).show()
                }
            }
        cameraPermissionResult.launch(android.Manifest.permission.CAMERA)

        // take selfie
        val selfiePhoto: Button = findViewById(R.id.selfie_button)
        selfiePhoto.setOnClickListener {
            captureImage()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menuIcon = menu.findItem(R.id.menu_item_one).actionView?.findViewById(R.id.menu_item_icon)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu ?: return super.onPrepareOptionsMenu(menu)
        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val imageUri = sharedPrefs.getString("image_uri", null)
        if (imageUri != null && menuIcon != null) {
            val savedIconUri = Uri.parse(imageUri)
            //toolbar image disappears when visibilty is Gone
            menuIcon?.visibility = View.VISIBLE
            Glide.with(this)
                .load(savedIconUri)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(menuIcon!!)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val imageUri = sharedPrefs.getString("image_uri", null)
        if (imageUri != null && menuIcon != null) {
            val savedIconUri = Uri.parse(imageUri)
            menuIcon?.visibility = View.GONE
            Glide.with(this)
                .load(savedIconUri.path)
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(menuIcon!!)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@ImagePreview, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun startCamera() {
        val preview: PreviewView = findViewById(R.id.selfie_camera_preview)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val previewShow = Preview.Builder().build()
            previewShow.setSurfaceProvider(preview.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
                cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, previewShow, imageCapture)
            } catch (e: Exception) {
                Log.e(TAG, "Error starting camera: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        imageCapture?.let { imageCapture ->
            val outputDirectory = getOutputDirectory()
            val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                        imageUri = savedUri

                        // save the image Uri to SharedPreferences
                        val sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                        sharedPrefs.edit().putString("image_uri", savedUri.toString()).apply()

                        // show the saved image in the menu icon
                        Glide.with(this@ImagePreview)
                            .load(savedUri)
                            .circleCrop()
                            .placeholder(R.drawable.user)
                            .into(menuIcon!!)

                        // show a message that the image has been saved
                        Toast.makeText(this@ImagePreview, "Image saved", Toast.LENGTH_SHORT).show()
                    }


                    override fun onError(exception: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                    }
                }
            )
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

}