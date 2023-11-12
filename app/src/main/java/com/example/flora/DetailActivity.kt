package com.example.flora

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.flora.databinding.DetailActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.*
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class DetailActivity : AppCompatActivity() {
    val binding by lazy { DetailActivityBinding.inflate(layoutInflater) }

    var flowerImg: String? = null
    val dialog = LoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var dataNo = intent.getStringExtra("dataNo")?.toInt()
        getFlower(dataNo)

        binding.next.setOnClickListener {
            dataNo = dataNo!! + 1
            getFlower(dataNo)
        }
        binding.before.setOnClickListener {
            dataNo = dataNo!! - 1
            getFlower(dataNo)
        }

        // 구매버튼 클릭 시
        binding.buy.setOnClickListener {
            // 로그인 상태 여부 확인
            if (MySharedPreferences.getLoginStatus(this).isNullOrBlank()) { // 비로그인 상태
                val builder = AlertDialog.Builder(this)
                builder.setTitle("알림")
                    .setMessage(
                        "로그인 후 이용이 가능합니다.\n" +
                                "로그인 페이지로 이동하시겠습니까?"
                    )
                    .setPositiveButton("네",
                        DialogInterface.OnClickListener { dialog, id ->
                            val loginPage = Intent(baseContext, LoginActivity::class.java)
                            startActivity(loginPage)
                        })
                    .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, id ->

                    })
                builder.show()
            } else {  // 로그인 상태
                // 주문 페이지로 이동
                val shopPage = Intent(baseContext, ShopActivity::class.java)
                shopPage.putExtra("name", binding.flowerName.text.toString())
                shopPage.putExtra("img", flowerImg)
                startActivity(shopPage)
            }
        }

        binding.addCart.setOnClickListener {
            // 로그인 상태 여부 확인
            // 비로그인 상태
            if (MySharedPreferences.getLoginStatus(this).isNullOrBlank()) {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("알림")
                    .setMessage(
                        "로그인 후 이용이 가능합니다.\n" +
                                "로그인 페이지로 이동하시겠습니까?"
                    )
                    .setPositiveButton("네",
                        DialogInterface.OnClickListener { dialog, id ->
                            val loginPage = Intent(baseContext, LoginActivity::class.java)
                            startActivity(loginPage)
                        })
                    .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, id ->

                    })
                builder.show()
            } else {  // 로그인 상태
                // 주문자 정보 필드는 로그인 시 저장된 정보를 가져와 삽입
                var userInfo: MutableList<UserData> = mutableListOf()
                userInfo = MySharedPreferences.getLogin(this)

                // 서버로 넘길 객체 생성 flowername, price, img, username
                var flowername = binding.flowerName.text.toString()
                var price = 10000
                var img = flowerImg.toString()
                var userName = userInfo.get(0).userName

                var cartData = CartModel(
                    flowername,
                    price,
                    img,
                    userName
                )

                // cartEntity에 삽입 Retrofit를 이용해 서버와 통신.
                RetrofitObject.getServerService().addCart(cartData)
                    .enqueue(object : retrofit2.Callback<CartAddResp> {
                        override fun onResponse(
                            call: Call<CartAddResp>,
                            response: Response<CartAddResp>
                        ) {
                            if (response.body()?.data == null) {
                                Toast.makeText(
                                    baseContext,
                                    "장바구니 추가에 실패 하였습니다. 잠시 후 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val builder = AlertDialog.Builder(this@DetailActivity)
                                builder.setTitle("알림")
                                    .setMessage("장바구니에 추가 하였습니다.")
                                    .setPositiveButton("장바구니로 이동",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            val cartPage = Intent(baseContext, ShopBagActivity::class.java)
                                            cartPage.putExtra("userName", userName)
                                            startActivity(cartPage)
                                        })
                                    .setNegativeButton("닫기",
                                        DialogInterface.OnClickListener { dialog, id ->
                                        })
                                builder.show()
                            }
                        }

                        override fun onFailure(call: Call<CartAddResp>, t: Throwable) {
                            Log.d("fail", t.message.toString())
                        }
                    })
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val login = findViewById<Button>(R.id.login_page)  // 로그인 버튼
        var loginBtnText = findViewById<Button>(R.id.login_page)  // 로그인 버튼 속 택스트

        // Activity 이동을 위한 Intent 객체
        val mainAvtivity = Intent(baseContext, MainActivity::class.java)

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
            if (loginBtnText.text.toString().equals("LOGIN")) {
                // 로그인 버튼 클릭 시
                val toLogin = Intent(baseContext, LoginActivity::class.java)
                startActivity(toLogin)
            } else {
                // 로그아웃 버튼 클릭 시
                MySharedPreferences.setLogout(this)
                loginBtnText.setText("LOGIN")
            }
        }

        // 헤더 플로라 로고 버튼 클릭 시
        val home = findViewById<Button>(R.id.home)
        home.setOnClickListener {
            startActivity(mainAvtivity)
            finish()
        }

        // 검색 버튼 클릭 시
        val search = findViewById<Button>(R.id.search)
        search.setOnClickListener {
            val searchPage = Intent(baseContext, SearchActivity::class.java)
            startActivity(searchPage)
        }

        // Footer
        // List 버튼 클릭 시
        val list = findViewById<ImageButton>(R.id.f_list)
        list.setOnClickListener {
            val footerList = Intent(baseContext, FlowerListActivity::class.java)
            startActivity(footerList)
        }

        // Home 버튼 클릭 시
        val fHome = findViewById<ImageButton>(R.id.f_home)
        fHome.setOnClickListener {
            startActivity(mainAvtivity)
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
                    .setMessage(
                        "로그인 후 이용이 가능합니다.\n" +
                                "로그인 페이지로 이동하시겠습니까?"
                    )
                    .setPositiveButton("네",
                        DialogInterface.OnClickListener { dialog, id ->
                            val loginPage = Intent(baseContext, LoginActivity::class.java)
                            startActivity(loginPage)
                        })
                    .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, id ->

                    })
                builder.show()
            } else {
                // 로그인 상태 -> 마이 페이지로 이동
                val goMyPage = Intent(baseContext, MyPageActivity::class.java)
                startActivity(goMyPage)
            }
        }
        // End Footer
    }

    private fun getFlower(dataNo: Int?) {
        dialog.show(supportFragmentManager, dialog.tag)

        if (dataNo != null) {
            RetrofitObject.getService().getFlower(dataNo).enqueue(object : Callback<Flower> {
                override fun onResponse(call: Call<Flower>, response: Response<Flower>) {
                    if (response.isSuccessful) {
                        // 통신 성공
                        dialog.dismiss()
                        setData(response.body())
                        Log.d("sucess", "Sucess Load Detail")
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
    }

    // 상세 페이지 데이터 매칭
    private fun setData(body: Flower?) {
        binding.detailView.visibility = View.VISIBLE
        if (body != null) {
            binding.flowerName.setText(body.root.result.get(0).name.toString() + "")
            binding.flowerEngname.setText(body.root.result.get(0).engname.toString() + "")
            binding.flowerLang.setText(body.root.result.get(0).flowLang.toString() + "")
            binding.flowerExplain.setText(body.root.result.get(0).content.toString() + "")
            binding.flowerUse.setText(body.root.result.get(0).used.toString() + "")
            binding.flowerRaise.setText(body.root.result.get(0).grow.toString() + "")
            binding.publishOrg.setText(body.root.result.get(0).publishOrg.toString() + "")

            disableSslVerification()
            // 이미지 가져오기
            val url = body.root.result.get(0).file1.toString()
            flowerImg = url
            Glide.with(this)
                .load(url)
                .override(190, 164)
                .into(binding.flowerImage)
        }
    }

    // http접속 오류 해결을 위한 메소드
    private fun disableSslVerification() {
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
