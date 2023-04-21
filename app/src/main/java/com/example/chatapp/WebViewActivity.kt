package com.example.chatapp

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    private lateinit var progressBar:ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view_layout)

        //this is to open external browser intent with your application and this will open new activity with another applicaiton
        /* val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://help.tagrain.com"))
         browserIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
         startActivity(browserIntent)*/

        val webView:WebView = findViewById(R.id.web_view)
        progressBar = findViewById(R.id.web_progressBar)

        webView.loadUrl("http://www.webpagetest.org/test")

        val settings = webView.settings
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.javaScriptEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.databaseEnabled = true
        settings.domStorageEnabled = true
        settings.setGeolocationEnabled(true)
        settings.saveFormData = true
        settings.allowFileAccess = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = true
        webView.webViewClient = webViewClient

    }

    private var webViewClient: WebViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }
    }
}


/*
class WebViewActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view_layout)

        val webView: WebView = findViewById(R.id.web_view)
        progressBar = findViewById(R.id.web_progressBar)
        webView.loadUrl("https://www.codexpedia.com/android/create-and-run-junit-tests-in-android-studio/")

        webView.webViewClient = MyWebViewClient

        webView.settings.supportZoom()
    }

    private var MyWebViewClient : WebViewClient = object : WebViewClient(){
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            //view?.loadUrl("www.facebook.com")
            return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }
    }
}*/
