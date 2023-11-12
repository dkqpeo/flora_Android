package com.example.flora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flora.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity: AppCompatActivity() {
    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            val userId = binding.id.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (userId.length != 0 && password.length != 0){ // 로그인 및 패스워드 폼에 데이터를 넣었을 때 실행
                val data = LoginModel(userId, password)

                RetrofitObject.getServerService().userLogin(data).enqueue(object: Callback<UserResp>{
                    override fun onResponse(call: Call<UserResp>, response: Response<UserResp>) {
                        Log.d("login","서버 통신 성공")
                        if(response.body()?.data == null)
                            Toast.makeText(baseContext, "아이디 혹은 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        else{
                            MySharedPreferences.setLogin(baseContext, "login", response.body()?.data!!)
                            Toast.makeText(baseContext, "로그인 성공! 안녕하세요 ${response.body()!!.data.userName.toString()} 님!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                    override fun onFailure(call: Call<UserResp>, t: Throwable) {
                        Log.d("fail", t.message.toString())
                    }
                })
            } else
                Toast.makeText(baseContext, "모든 내용을 채워주세요!", Toast.LENGTH_SHORT).show()

        }

        // 플로라 로고 버튼 클릭 시
        val home = findViewById<Button>(R.id.home)
        home.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        // 검색 버튼 클릭 시
        val search = findViewById<Button>(R.id.search)
        search.setOnClickListener {
            val searchPage = Intent(baseContext, SearchActivity::class.java)
            startActivity(searchPage)
        }

        binding.join.setOnClickListener {
            val joinPage = Intent(baseContext, JoinAvtivity::class.java)
            startActivity(joinPage)
        }
    }
}