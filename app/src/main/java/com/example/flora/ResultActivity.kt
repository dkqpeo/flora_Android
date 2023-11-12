package com.example.flora

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flora.databinding.ActivitySearchResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultActivity : AppCompatActivity() {
    val binding by lazy { ActivitySearchResultBinding.inflate(layoutInflater) }
    var result: MutableList<Flist> = mutableListOf()
    val dialog = LoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var status = intent.getStringExtra("status")?.toInt()

        if (status == 1){
            var content = intent.getStringExtra("content").toString()
            searchFlowerName(content)
        }
        else if (status == 2){
            var content = intent.getStringExtra("content").toString()
            searchFlowerLang(content)
        }
        else if (status == 3){
            var month = intent.getStringExtra("month").toString()
            var day = intent.getStringExtra("day").toString()
            searchFlowerDate(month, day)
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
                // 로그인 상태 -> 마이 페이지로 이동
                val goMyPage = Intent(baseContext, MyPageActivity::class.java)
                startActivity(goMyPage)
            }
        }
        // End Footer
    }

    private fun searchFlowerName(content: String) {
        dialog.show(supportFragmentManager, dialog.tag)

        RetrofitObject.getService().getFlowerSearchName(content).enqueue(object : Callback<Flower> {
            override fun onFailure(call: Call<Flower>, t: Throwable) {
                Log.d("fail", t.message.toString())
            }

            override fun onResponse(call: Call<Flower>, response: Response<Flower>) {
                if (response.isSuccessful) {
                    // 통신 성공
                    dialog.dismiss()
                    setTodayFlower(response.body())
                } else {
                    // 내부 에러
                    Log.d("error", "error")
                }
            }
        })
    }

    private fun searchFlowerLang(content: String) {
        dialog.show(supportFragmentManager, dialog.tag)

        RetrofitObject.getService().getFlowerSearchLang(content).enqueue(object : Callback<Flower> {
            override fun onFailure(call: Call<Flower>, t: Throwable) {
                Log.d("fail", t.message.toString())
            }

            override fun onResponse(call: Call<Flower>, response: Response<Flower>) {
                if (response.isSuccessful) {
                    // 통신 성공
                    dialog.dismiss()
                    setTodayFlower(response.body())
                } else {
                    // 내부 에러
                    Log.d("error", "error")
                }
            }
        })
    }

    private fun searchFlowerDate(month: String, day: String) {
        dialog.show(supportFragmentManager, dialog.tag)

        RetrofitObject.getService().getFlowerSearchDate(month, day).enqueue(object : Callback<Flower> {
            override fun onFailure(call: Call<Flower>, t: Throwable) {
                Log.d("fail", t.message.toString())
            }

            override fun onResponse(call: Call<Flower>, response: Response<Flower>) {
                if (response.isSuccessful) {
                    // 통신 성공
                    dialog.dismiss()
                    setTodayFlower(response.body())
                } else {
                    // 내부 에러
                    Log.d("error", "error")
                }
            }
        })
    }

    private fun setTodayFlower(body: Flower?) {
        result.clear()
        if (body != null)
            for (i in body.root.result) {
                var dataNo: String = i.dataNo.toString()
                var name: String = i.name.toString()
                var lang = i.flowLang.toString()
                var image: String = i.file1.toString()
                var flist = Flist(dataNo, name, lang, image)
                result.add(flist)
            }
        createApapter()
    }

    private fun createApapter() {
        var adapter = CustomAdapter()

        adapter.listdata = result
        binding.recyleView.adapter = adapter
        binding.recyleView.layoutManager = LinearLayoutManager(this)
        binding.listFooter.visibility = View.VISIBLE
    }
}