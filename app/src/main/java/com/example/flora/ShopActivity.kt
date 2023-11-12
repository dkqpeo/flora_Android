package com.example.flora

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.flora.databinding.ActivityShopBinding
import com.tosspayments.paymentsdk.PaymentWidget
import com.tosspayments.paymentsdk.model.PaymentCallback
import com.tosspayments.paymentsdk.model.PaymentWidgetStatusListener
import com.tosspayments.paymentsdk.model.TossPaymentResult
import com.tosspayments.paymentsdk.view.PaymentMethod
import retrofit2.Call
import retrofit2.Response
import java.io.Serializable
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ShopActivity : AppCompatActivity(), Serializable {
    private lateinit var binding: ActivityShopBinding
    val charset = ('0'..'9') + ('a'..'z') + ('A'..'Z')      // 주문번호 임의 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상품 명 필드
        binding.flowerName.text = intent.getStringExtra("name")

        disableSslVerification()
        // 이미지 가져오기
        val url = intent.getStringExtra("img")
        Glide.with(this)
            .load(url)
            .override(190, 164)
            .into(binding.flowerImage)

        // 주문자 정보 필드는 로그인 시 저장된 정보를 가져와 삽입
        var userInfo: MutableList<UserData> = mutableListOf()
        userInfo = MySharedPreferences.getLogin(this)

        binding.name.setText(userInfo.get(0).userName)
        binding.phone.setText(userInfo.get(0).tel)
        binding.email.setText(userInfo.get(0).email)

        binding.postNum.setOnClickListener {
            val kakao = Intent(this, KakaoMapActivity::class.java)
            getSearchResult.launch(kakao)
        }

        val orderId = List(15) {charset.random()}.joinToString("")
        val flowerName = intent.getStringExtra("name").toString()
        val userName = binding.name.text.toString()
        val fromName = binding.fromName.text.toString().trim()
        val fromTel = binding.fromPhone.text.toString().trim()
        val postNum = binding.postNum.text.toString().trim()
        val address = binding.fromAddress.text.toString().trim()
        val detailAddress = binding.fromDetainAddress.text.toString().trim()
        val price = binding.price.text.toString().toInt()

        // 토스 결제 위젯
        val paymentWidget = PaymentWidget(
            activity = this@ShopActivity,
            clientKey = "test_ck_kYG57Eba3Gbq1MEAen9Q8pWDOxmA",
            customerKey = "PaymentWidget.ANONYMOUS",
        )

        val paymentMethodWidgetStatusListener = object : PaymentWidgetStatusListener {
            override fun onLoad() {
                val message = "결제위젯 렌더링 완료"
                Log.d("PaymentWidgetStatusListener", message)
            }
        }

        paymentWidget.run {
            renderPaymentMethods(
                method = binding.paymentMethodWidget,
                amount = PaymentMethod.Rendering.Amount(price),
                paymentWidgetStatusListener = paymentMethodWidgetStatusListener
            )

            renderAgreement(binding.agreementWidget)
        }

        // 결제하기 버튼 클릭 시
        binding.requestPaymentCta.setOnClickListener {

            // 모든 영역에 값을 넣었을 때 결제 진행
            if(fromName.isNotEmpty() && fromTel.isNotEmpty() && postNum.isNotEmpty() && address.isNotEmpty() && detailAddress.isNotEmpty()) {

                paymentWidget.requestPayment(
                    paymentInfo = PaymentMethod.PaymentInfo(orderId , flowerName),
                    paymentCallback = object : PaymentCallback {

                        // 결제가 완료 되었을 경우 토스 페이먼트에 승인 요청을 보냄.
                        override fun onPaymentSuccess(success: TossPaymentResult.Success) {
                            val paymentKey = success.paymentKey

                            Log.i("toss", success.paymentKey)
                            Log.i("toss", success.orderId)
                            Log.i("toss", success.amount.toString())

                            // 레트로핏을 이용해 토스 서버에 결제 승인 요청.
                            var reqPayment = PaymentModel(success.paymentKey, success.paymentKey, success.paymentKey.toInt())

                            RetrofitObject.getTossService().reqPayment(reqPayment)
                                .enqueue(object : retrofit2.Callback<TossResp>{

                                    // 승인 요청이 정상 처리된경우
                                    override fun onResponse(
                                        call: Call<TossResp>,
                                        response: Response<TossResp>
                                    ) {
                                        // 토스로부터 Response 데이터가 정상적으로 넘어왔을 떄 DB에 주문데이터 삽입 진행.
                                        if(response.body()?.mId != null){
                                            val paymentKey = success.paymentKey

                                            val orderReq = OrderModel(orderId, flowerName, price, intent.getStringExtra("img").toString(),
                                                userName, postNum, address, detailAddress, fromName, fromTel, paymentKey)

                                            // DB에 주문 데이터 삽입
                                            RetrofitObject.getServerService().createOrder(orderReq)
                                                .enqueue(object : retrofit2.Callback<OrderResp>{
                                                    override fun onResponse(
                                                        call: Call<OrderResp>,
                                                        response: Response<OrderResp>
                                                    ) {
                                                        if(response.body()?.data != null){
                                                            val builder = AlertDialog.Builder(this@ShopActivity)
                                                            builder.setTitle("알림")
                                                                .setMessage("주문이 완료 되었습니다.")
                                                                .setPositiveButton("닫기",
                                                                    DialogInterface.OnClickListener { dialog, id ->
                                                                        finish()
                                                                    })
                                                            builder.show()
                                                        }
                                                        else{
                                                            Toast.makeText(
                                                                baseContext,
                                                                "주문을 실패 하였습니다. 잠시 후 다시 시도해주세요.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<OrderResp>, t: Throwable) {
                                                        Log.e("fail_orderInsertDB", t.message.toString())
                                                    }
                                                })
                                        }
                                        else{
                                            Toast.makeText(
                                                baseContext,
                                                "주문을 실패 하였습니다. 잠시 후 다시 시도해주세요.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    // 토스 서버 승인에 오류가 난 경우
                                    override fun onFailure(call: Call<TossResp>, t: Throwable) {
                                        Log.e("tossFail", t.message.toString())
                                    }
                                })
                        }

                        // 결제에 실패한 경우
                        override fun onPaymentFailed(fail: TossPaymentResult.Fail) {
                            Log.e("tossFail",fail.errorMessage)
                        }
                    }
                )
            }

            // 사용자가 데이터를 비워둔 상태로 결제를 시도하는 경우
            else {
                Toast.makeText(baseContext, "정보를 모두 채워주세요!", Toast.LENGTH_SHORT).show()
            }

        }

/*        // 결제하기 버튼 클릭 시
        binding.submitOrder.setOnClickListener {
            val flowerName = intent.getStringExtra("name").toString()
            val userName = binding.name.text.toString()
            val fromName = binding.fromName.text.toString().trim()
            val fromTel = binding.fromPhone.text.toString().trim()
            val postNum = binding.postNum.text.toString().trim()
            val address = binding.fromAddress.text.toString().trim()
            val detailAddress = binding.fromDetainAddress.text.toString().trim()
            val price = binding.price.text.toString().toInt()

            // 구매하기 버튼 클릭 시 빈칸 여부 확인
            if (fromName.isNotEmpty() && fromTel.isNotEmpty() && postNum.isNotEmpty() && address.isNotEmpty() && detailAddress.isNotEmpty()) {
                // 모든 내용이 채워졌을 때
                val orderData = OrderModel(
                    flowerName,
                    price,
                    intent.getStringExtra("img").toString(),
                    userName,
                    postNum,
                    address,
                    detailAddress,
                    fromName,
                    fromTel
                )

                // RetriFit를 이용해 서버와 통신을 함.
                RetrofitObject.getServerService().createOrder(orderData)
                    .enqueue(object : retrofit2.Callback<OrderResp> {
                        override fun onResponse(
                            call: Call<OrderResp>,
                            response: Response<OrderResp>
                        ) {
                            if (response.body()?.data == null) {
                                Toast.makeText(
                                    baseContext,
                                    "주문을 실패 하였습니다. 잠시 후 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val builder = AlertDialog.Builder(this@ShopActivity)
                                builder.setTitle("알림")
                                    .setMessage("주문이 완료 되었습니다.")
                                    .setPositiveButton("닫기",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            finish()
                                        })
                                builder.show()
                            }
                        }

                        override fun onFailure(call: Call<OrderResp>, t: Throwable) {
                            Log.d("fail", t.message.toString())
                        }
                    })
            } else // 빈칸이 존재할 경우
                Toast.makeText(baseContext, "모든 내용을 채워주세요!", Toast.LENGTH_SHORT).show()
        }

        // 결제 버튼 클릭 시 토스 페이지로 이동
        binding.submitOrder.setOnClickListener {
            // val orderId = List(15) {charset.random()}.joinToString("")
            val flowerName = intent.getStringExtra("name").toString()
            val userName = binding.name.text.toString()
            val fromName = binding.fromName.text.toString().trim()
            val fromTel = binding.fromPhone.text.toString().trim()
            val postNum = binding.postNum.text.toString().trim()
            val address = binding.fromAddress.text.toString().trim()
            val detailAddress = binding.fromDetainAddress.text.toString().trim()
            val price = binding.price.text.toString().toInt()

            // 구매하기 버튼 클릭 시 빈칸 여부 확인
            if (fromName.isNotEmpty() && fromTel.isNotEmpty() && postNum.isNotEmpty() && address.isNotEmpty() && detailAddress.isNotEmpty()) {
                // 모든 내용이 채워졌을 때 상품 명, 주문자 명, 받는이 명, 받는이 전화번호, 우편번호 및 주소를 담음
                val tossData = TossModel(
                    orderId,
                    flowerName,
                    price,
                    intent.getStringExtra("img").toString(),
                    userName,
                    postNum,
                    address,
                    detailAddress,
                    fromName,
                    fromTel
                )
                var orderInfo : ArrayList<TossModel> = arrayListOf()
                orderInfo.add(tossData)
                Log.d("orderSize", orderInfo.size.toString())


                var toss = Intent(this, TossTest::class.java)
                intent.putExtra("orderInfo", orderInfo)
                startActivity(toss)

                // RetriFit를 이용해 서버와 통신을 함.
                RetrofitObject.getServerService().createOrder(orderData)
                    .enqueue(object : retrofit2.Callback<OrderResp> {
                        override fun onResponse(
                            call: Call<OrderResp>,
                            response: Response<OrderResp>
                        ) {
                            if (response.body()?.data == null) {
                                Toast.makeText(
                                    baseContext,
                                    "주문을 실패 하였습니다. 잠시 후 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val builder = AlertDialog.Builder(this@ShopActivity)
                                builder.setTitle("알림")
                                    .setMessage("주문이 완료 되었습니다.")
                                    .setPositiveButton("닫기",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            finish()
                                        })
                                builder.show()
                            }
                        }

                        override fun onFailure(call: Call<OrderResp>, t: Throwable) {
                            Log.d("fail", t.message.toString())
                        }
                    })

            } else // 빈칸이 존재할 경우
                Toast.makeText(baseContext, "모든 내용을 채워주세요!", Toast.LENGTH_SHORT).show()
        }*/

    }

    // 카카오 주소 입력 API
    private val getSearchResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { results ->
            if (results.resultCode == RESULT_OK) {
                if (results.data != null) {
                    val data = results.data!!.getStringExtra("data")
                    val postNum = results.data!!.getStringExtra("zonecode")

                    binding.postNum.setText(postNum)
                    binding.fromAddress.setText(data)

                    Log.d("post Api Sucess", data.toString())
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

        // 로그아웃 이밴트 처리
        login.setOnClickListener {
            // 로그아웃 버튼 클릭 시
            MySharedPreferences.setLogout(this)
            loginBtnText.setText("LOGIN")
            finish()
            startActivity(mainAvtivity)
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