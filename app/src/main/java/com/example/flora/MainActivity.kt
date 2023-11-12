package com.example.flora

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.flora.databinding.ActivityListItemBinding
import com.example.flora.databinding.ActivityMainBinding
import com.example.flora.databinding.HeaderBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Objects
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getTodayFlower()
    }

    override fun onStart() {
        super.onStart()

        // 로그인 버튼
        val login = findViewById<Button>(R.id.login_page)
        // 로그인 버튼 속 택스트
        var loginBtnText = findViewById<Button>(R.id.login_page)

        // 로그인 여부 확인
        if (MySharedPreferences.getLoginStatus(this).isNullOrBlank()) {
            // 비로그인 상태
            loginBtnText.setText("LOGIN")
        } else {
            // 로그인 상태
            loginBtnText.setText("LOGOUT")
        }

        // 로그인 로그아웃 버튼 이밴트 처리
        login.setOnClickListener {
            if(loginBtnText.text.toString().equals("LOGIN")){
                // 로그인 버튼 클릭 시
                val toLogin = Intent(baseContext, LoginActivity::class.java)
                startActivity(toLogin)
            } else{
                // 로그아웃 버튼 클릭 시
                MySharedPreferences.setLogout(this)
                loginBtnText.setText("LOGIN")
            }
        }

        // 검색 버튼 클릭 시
        val search = findViewById<Button>(R.id.search)
        search.setOnClickListener {
            val searchPage = Intent(baseContext, SearchActivity::class.java)
            startActivity(searchPage)
        }

        // 꽃 목록 리스트로 이동
/*        binding.list.setOnClickListener(View.OnClickListener {
            val intent = Intent(baseContext, FlowerListActivity::class.java)
            startActivity(intent)
        })*/

        // 메인화면의 오늘의 꽃 클릭 시 상세보기로 이동
        binding.mainTo.setOnClickListener {
            val detail = Intent(baseContext, DetailActivity::class.java)
            //Log.d("dataNo", binding.dataNo.text.toString())
            detail.putExtra("dataNo", binding.dataNo.text.toString())
            startActivity(detail)
        }

        // Footer Area

        // List 버튼 클릭 시
        val list = findViewById<ImageButton>(R.id.f_list)
        list.setOnClickListener {
            val footerList = Intent(baseContext, FlowerListActivity::class.java)
            startActivity(footerList)
        }

        // 장바구니 버튼 클릭 시
        val cart =findViewById<ImageButton>(R.id.cart)
        cart.setOnClickListener {
            // 로그인 상태 여부 확인
            if (MySharedPreferences.getLoginStatus(this).isNullOrBlank()) {
                // 비로그인 상태
                val builder = AlertDialog.Builder(this)
                builder.setTitle("알림")
                    .setMessage("로그인 후 이용이 가능합니다.\n" +
                            "로그인 페이지로 이동하시겠습니까?")
                    .setPositiveButton("네",
                        DialogInterface.OnClickListener{ dialog, id ->
                            val loginPage = Intent(baseContext, LoginActivity::class.java)
                            startActivity(loginPage)
                        })
                    .setNegativeButton("아니요", DialogInterface.OnClickListener{ dialog, id ->
                    })

                builder.show()
            } else {
                // 로그인 상태
                // 장바구니 페이지로 이동
                val cartPage = Intent(baseContext, ShopBagActivity::class.java)
                startActivity(cartPage)
            }
        }

        // MyPage 버튼 클릭 시
        val myPage = findViewById<ImageButton>(R.id.f_mypage)
        myPage.setOnClickListener {

            // 로그인 상태 여부 확인
            if (MySharedPreferences.getLoginStatus(this).isNullOrBlank()) {
                // 비로그인 상태
                val builder = AlertDialog.Builder(this)
                builder.setTitle("알림")
                    .setMessage("로그인 후 이용이 가능합니다.\n" +
                            "로그인 페이지로 이동하시겠습니까?")
                    .setPositiveButton("네",
                        DialogInterface.OnClickListener{ dialog, id ->
                            val loginPage = Intent(baseContext, LoginActivity::class.java)
                            startActivity(loginPage)
                        })
                    .setNegativeButton("아니요", DialogInterface.OnClickListener{ dialog, id ->
                    })

                builder.show()
            } else {
                // 로그인 상태
                // 주문 페이지로 이동
                val goMyPage = Intent(baseContext, MyPageActivity::class.java)
                startActivity(goMyPage)
            }
        }
    }

    // 오늘의 꽃 조회
    private fun getTodayFlower() {
        RetrofitObject.getService().getTodayFlower().enqueue(object : Callback<Flower> {

            override fun onResponse(call: Call<Flower>, response: Response<Flower>) {
                if (response.isSuccessful) {
                    // 통신 성공
                    setTodayFlower(response.body())
                    Log.d("sucess", "통신 성골")
                } else {
                    // 내부 에러
                    Log.d("error", "error")
                }
            }

            override fun onFailure(call: Call<Flower>, t: Throwable) {
                // api 접속 실패
                Log.d("fail", t.message.toString())
            }
        })
    }

    private fun setTodayFlower(body: Flower?) {

        if (body != null) {
            binding.flowerName.setText(body.root.result.get(0).name.toString())
            binding.flowerEngname.setText(body.root.result.get(0).engname.toString())
            binding.flowerLang.setText(body.root.result.get(0).flowLang.toString())
            binding.dataNo.setText(body.root.result.get(0).dataNo.toString())

            // 이미지 가져오기
            disableSslVerification()
            val url = body.root.result.get(0).file1.toString()
            Glide.with(this)
                .load(url)
                .into(binding.flowerImage)
        }
    }

    // http접속 오류 해결을 위한 메소드
    private fun disableSslVerification() {
        // TODO Auto-generated method stub
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate>? {
                        return null
                    }

                    override fun checkClientTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun checkServerTrusted(
                        certs: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }
                }
            )

            // Install the all-trusting trust manager
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

            // Create all-trusting host name verifier
            val allHostsValid =
                HostnameVerifier { hostname, session -> true }

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}