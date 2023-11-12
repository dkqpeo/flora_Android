package com.example.flora

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.example.flora.databinding.ActivityMyPageBinding
import org.simpleframework.xml.Order

class MyPageActivity : AppCompatActivity() {
    val binding by lazy { ActivityMyPageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 주문자 정보 필드는 로그인 시 저장된 정보를 가져와 삽입
        var userInfo:MutableList<UserData> = mutableListOf()
        userInfo = MySharedPreferences.getLogin(this)

        // 레이아웃 이름 영역에 사용자 이름을 넣어줌
        binding.userName.setText(userInfo.get(0).userName)

        // 수정버튼 클릭 시
        binding.editInfo.setOnClickListener {
            val editInfo = Intent(baseContext, ChangeInfoActivity::class.java)
            startActivity(editInfo)
        }

        // 주문 상세 버튼 클릭 시
        binding.orderDetail.setOnClickListener {
            val orderDetail = Intent(baseContext, OrderDetail::class.java)
            startActivity(orderDetail)
        }
    }

    override fun onStart() {
        super.onStart()

        val login = findViewById<Button>(R.id.login_page)  // 로그인 버튼
        var loginBtnText = findViewById<Button>(R.id.login_page)  // 로그인 버튼 속 택스트

        // Activity 이동을 위한 Intent 객체
        val mainAvtivity = Intent(baseContext, MainActivity::class.java)

        // 로그인 여부 확인
        if (MySharedPreferences.getLoginStatus(this).isNullOrBlank()) { // 비로그인 상태
            /*binding.statusLogout.visibility = View.VISIBLE
            binding.statusLogin.visibility = View.GONE*/
            loginBtnText.setText("LOGIN")
        } else {                                            // 로그인 상태
            /*binding.statusLogout.visibility = View.GONE
            binding.statusLogin.visibility = View.VISIBLE*/
            loginBtnText.setText("LOGOUT")
        }

        // 로그인 로그아웃 버튼 이밴트 처리
        login.setOnClickListener {
            // 로그아웃 버튼 클릭 시
            MySharedPreferences.setLogout(this)
            loginBtnText.setText("LOGIN")
            finish()
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
                // 로그인 상태 -> 주문 페이지로 이동
                val goMyPage = Intent(baseContext, MyPageActivity::class.java)
                startActivity(goMyPage)
            }
        }
        // End Footer
    }
}