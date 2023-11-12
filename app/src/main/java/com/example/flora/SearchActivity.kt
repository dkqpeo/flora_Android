package com.example.flora

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.flora.databinding.SearchActivityBinding


class SearchActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val binding by lazy { SearchActivityBinding.inflate(layoutInflater) }
    var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 스피너 목록 선택 이벤트 리스너
        binding.dropBox.onItemSelectedListener = this

        // 날짜 검색에서 날짜 부분 클릭 시
        binding.searchDate.setOnClickListener {
            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.select_day, null)

            val month: NumberPicker = mView.findViewById(R.id.search_month)
            val day: NumberPicker = mView.findViewById(R.id.search_day)
            val cancel: Button = mView.findViewById(R.id.cancel_button)
            val save: Button = mView.findViewById(R.id.save_button)
            //  순환 안되게 막기
            month.wrapSelectorWheel = false
            day.wrapSelectorWheel = false

            //  editText 설정 해제
            month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            day.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            // 최소값 설정
            month.minValue = 1
            day.minValue = 1

            // 최대값 설정
            month.maxValue = 12
            day.maxValue = 31

            //  취소 버튼 클릭 시
            cancel.setOnClickListener {
                dialog.dismiss()
                dialog.cancel()
            }

            //  완료 버튼 클릭 시
            save.setOnClickListener {
                binding.searchText2.visibility = View.GONE
                binding.searchDate.visibility = View.VISIBLE

                binding.searchMonth.setText((month.value).toString())
                binding.searchDay.setText((day.value).toString())

                dialog.dismiss()
                dialog.cancel()

                status = "3"
            }

            dialog.setView(mView)
            dialog.create()
            dialog.show()
        }

        // 검색하기 버튼 클릭 시
        binding.searchBtn.setOnClickListener {
            val intent = Intent(baseContext, ResultActivity::class.java)

            if (binding.searchText2.isVisible) {  // 꽃 이름, 꽃말 검색 시
                var content = binding.searchText2.text.toString().trim()
                intent.putExtra("status", status)
                intent.putExtra("content", content)
                startActivity(intent)
            } else if (binding.searchMonth.isVisible) { // 날짜로 검색 시
                var month = binding.searchMonth.text
                var day = binding.searchDay.text

                intent.putExtra("status", status)
                intent.putExtra("month", month)
                intent.putExtra("day", day)
                startActivity(intent)
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
        val cart = findViewById<ImageButton>(R.id.cart)
        cart.setOnClickListener {
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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p2 == 0 || p2 == 1) {
            binding.searchText2.visibility = View.VISIBLE
            binding.searchDate.visibility = View.GONE

            if (p2 == 0)
                status = "1"
            else
                status = "2"
        } else if (p2 == 2) {
            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.select_day, null)

            val month: NumberPicker = mView.findViewById(R.id.search_month)
            val day: NumberPicker = mView.findViewById(R.id.search_day)
            val cancel: Button = mView.findViewById(R.id.cancel_button)
            val save: Button = mView.findViewById(R.id.save_button)
            //  순환 안되게 막기
            month.wrapSelectorWheel = false
            day.wrapSelectorWheel = false

            //  editText 설정 해제
            month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            day.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

            // 최소값 설정
            month.minValue = 1
            day.minValue = 1

            // 최대값 설정
            month.maxValue = 12
            day.maxValue = 31

            //  취소 버튼 클릭 시
            cancel.setOnClickListener {
                dialog.dismiss()
                dialog.cancel()

                if (p0 != null) {
                    p0.setSelection(0)
                }
            }

            //  완료 버튼 클릭 시
            save.setOnClickListener {
                binding.searchText2.visibility = View.GONE
                binding.searchDate.visibility = View.VISIBLE

                binding.searchMonth.setText((month.value).toString())
                binding.searchDay.setText((day.value).toString())

                dialog.dismiss()
                dialog.cancel()

                status = "3"
            }

            dialog.setView(mView)
            dialog.create()
            dialog.show()
        }
    }

    // 스피너에 선택된 값이 없을때 0번으로 초기 설정
    override fun onNothingSelected(p0: AdapterView<*>?) {
        if (p0 != null) {
            p0.setSelection(0)
        }
    }
}