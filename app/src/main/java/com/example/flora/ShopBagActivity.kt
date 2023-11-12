package com.example.flora

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flora.databinding.ActivityShopBagBinding
import retrofit2.Call
import retrofit2.Response

class ShopBagActivity : AppCompatActivity() {
    val binding by  lazy { ActivityShopBagBinding.inflate(layoutInflater) }
    var result: MutableList<CartData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // MySharedPreferences에 저장된 사용자 이름 가져오기
        var userInfo: MutableList<UserData> = mutableListOf()
        userInfo = MySharedPreferences.getLogin(this)
        var userName = userInfo.get(0).userName


        if (userName != null){
            // 사용자의 장바구니 목록 가져오기
            RetrofitObject.getServerService().cartList(userName)
                .enqueue(object : retrofit2.Callback<CartResp>{
                    override fun onResponse(call: Call<CartResp>, response: Response<CartResp>) {
                        setCartList(response.body())
                    }

                    override fun onFailure(call: Call<CartResp>, t: Throwable) {
                        Log.d("fail", t.message.toString())
                    }
                })
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

    private fun setCartList(body: CartResp?){
        result.clear()
        if(body != null)
            for (i in body.data){
                var seq = i.seq.toLong()
                var flowerName = i.flowerName.toString()
                var price = i.price.toInt()
                var flowerImg = i.flowerImg.toString()
                var userSeq = i.userSeq
                var cart = CartData(seq, flowerName, price, flowerImg, userSeq)
                result.add(cart)
        }
        createApapter()
    }

    private fun createApapter() {
        var adapter = CartAdapter(binding)

        adapter.listdata = result
        binding.recyleView.adapter = adapter
        binding.recyleView.layoutManager = LinearLayoutManager(this)

    }
}