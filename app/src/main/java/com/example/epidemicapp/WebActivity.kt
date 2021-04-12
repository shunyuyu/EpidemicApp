package com.example.epidemicapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.epidemicapp.databinding.ActivityWebBinding


class WebActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityWebBinding
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val url = intent.getStringExtra("url")
        mBinding.webView.settings.javaScriptEnabled=true
        mBinding.webView.webViewClient = WebViewClient()
        mBinding.webView.loadUrl(url.toString())
        //Toast.makeText(this, "您点击了:$url", Toast.LENGTH_SHORT).show()
    }
}