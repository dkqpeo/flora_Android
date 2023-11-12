package com.example.flora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class KakaoMapActivity : AppCompatActivity() {
    lateinit var browser: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kakao_map)

        browser = findViewById(R.id.webView)
        browser.settings.javaScriptEnabled = true

        browser.addJavascriptInterface(MyJavaScriptInterface(), "Android")

        browser.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                //page loading을 끝냈을 때 호출되는 콜백 메서드
                //안드로이드에서 자바스크립트 메서드 호출
                browser.loadUrl("javascript:sample2_execDaumPostcode();")
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }
        })
        //최초로 웹뷰 로딩
        browser.loadUrl("http://10.0.2.2:8081/kakaoMap.html")
    }

    inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun processDATA(data: String?, zoneCode: String) {
            Log.d("MyJavaScriptInterface", "Received data: $data"+ zoneCode)
            //자바 스크립트로 부터 다음 카카오 주소 검색 API 결과를 전달 받는다.
            //val extra = Bundle()
            val intent = Intent()

            intent.putExtra("data", data)
            intent.putExtra("zonecode", zoneCode)

            setResult(RESULT_OK, intent)
            finish()
        }
    }
}