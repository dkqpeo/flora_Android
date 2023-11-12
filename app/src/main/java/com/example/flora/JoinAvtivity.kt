package com.example.flora

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flora.databinding.ActivityJoinBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinAvtivity: AppCompatActivity() {
    val binding by lazy { ActivityJoinBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 회원가입 버튼 클릭 시
        binding.join.setOnClickListener {
            var userId: String = binding.id.text.toString().trim()
            var password: String = binding.password.text.toString().trim()
            var passwordCheck: String = binding.passwordCheck.text.toString().trim()
            var userName: String = binding.name.text.toString().trim()
            var tel: String = binding.phone.text.toString().trim()
            var email: String = binding.joinEmail.text.toString().trim()

            if(userId.length != 0 && password.length != 0 && passwordCheck.length != 0 && userName.length != 0 && tel.length != 0){
                if (password.equals(passwordCheck)){
                    val data = JoinModel(userId, password, userName, tel, email)
                    RetrofitObject.getServerService().userSignUp(data).enqueue(object: Callback<UserResp>{
                        override fun onResponse(call: Call<UserResp>, response: Response<UserResp>) {
                            Log.d("JoinService","서버 통신 성공")
                            if(response.body()?.data == null)
                                Toast.makeText(baseContext, "회원가입 실패! 다시 시도 해보세요.", Toast.LENGTH_SHORT).show()
                            else{
                                Toast.makeText(baseContext, "회원가입 성공! 로그인 페이지로 이동합니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        override fun onFailure(call: Call<UserResp>, t: Throwable) {
                            Log.d("joinFail", t.message.toString())
                        }
                    })
                } else{
                    Toast.makeText(baseContext, "비밀번호가 서로 일치하지 않습니다!", Toast.LENGTH_SHORT).show()
                }
            } else
                Toast.makeText(baseContext, "모든 내용을 채워주세요!", Toast.LENGTH_SHORT).show()
        }
    }
}