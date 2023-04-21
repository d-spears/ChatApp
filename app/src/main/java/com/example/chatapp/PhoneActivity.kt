package com.example.chatapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatapp.databinding.ChatPageLayoutBinding
import com.example.chatapp.databinding.PhoneLayoutBinding
import com.google.android.material.snackbar.Snackbar

class PhoneActivity : AppCompatActivity() {

    private lateinit var bind: PhoneLayoutBinding
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var textView: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = PhoneLayoutBinding.inflate(layoutInflater)
        setContentView(bind.root)

        textView = findViewById(R.id.phone_text_view)
        toolbar = findViewById(R.id.phone_toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Phone"
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val telephone: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val readPhonePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                val softwareVersion = if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                } else {
                    Toast.makeText(this@PhoneActivity, "This permission is required", Toast.LENGTH_LONG).show()
                    telephone.deviceSoftwareVersion
                }

                val stringBuilder = StringBuilder()
                val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val imei = telephonyManager.imei
                val subscriberId = telephonyManager.subscriberId
                val simSerialNumber = telephonyManager.simSerialNumber
                val networkCountryISO = telephonyManager.networkCountryIso

                stringBuilder.append("IMEI No - $imei\n")
                stringBuilder.append("SubscriberID - $subscriberId\n")
                stringBuilder.append("simSerialNumber - $simSerialNumber\n")
                stringBuilder.append("Network Country ISO - $networkCountryISO\n")
                stringBuilder.append("Software Version - $softwareVersion\n")
                stringBuilder.append("Phone Number - ${telephonyManager.line1Number}")

                textView.text = stringBuilder.toString()
            } else {
                Snackbar.make(toolbar, "The Camera permission is required for running this application", Snackbar.LENGTH_INDEFINITE)
                    .show()
            }
        }
        readPhonePermission.launch(android.Manifest.permission.READ_PHONE_STATE)
        }


    }

