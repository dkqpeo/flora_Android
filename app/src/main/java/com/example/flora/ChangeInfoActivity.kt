package com.example.flora

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.flora.databinding.ActivityChangeInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeInfoActivity : AppCompatActivity() {
    val binding by lazy { ActivityChangeInfoBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 주문자 정보 필드는 로그인 시 저장된 정보를 가져와 삽입
        var userInfo:MutableList<UserData> = mutableListOf()
        userInfo = MySharedPreferences.getLogin(this)

        // 이름 아이디 전화번호 영역에 저장된 데이터 삽업
        //binding.name.setText(userInfo.get(0).userName)
        //binding.id.setText(userInfo.get(0).userId)
        binding.phone.setText(userInfo.get(0).tel)
        binding.email.setText(userInfo.get(0).email)

        // 수정하기 버튼 클릭 시
        binding.update.setOnClickListener {
            val userSeq = userInfo.get(0).seq.toLong()
            val userId = userInfo.get(0).userId
            val name = userInfo.get(0).userName
            val oldPw = binding.oldPassword.text.toString().trim()
            val pw = binding.password.text.toString().trim()
            val pwChk = binding.passwordCheck.text.toString().trim()
            val tel = binding.phone.text.toString().trim()
            val email = binding.email.text.toString().trim()

            if (oldPw.length != 0 && pw.length != 0 && pwChk.length != 0 && tel.length != 0){
                if(pw.equals(pwChk)){
                    val data = UpdateUserModel(userSeq, userId, name, oldPw, pw, tel, email)

                    RetrofitObject.getServerService().updateUser(data).enqueue(object: Callback<UserResp>{
                        override fun onResponse(call: Call<UserResp>, response: Response<UserResp>) {
                            Log.d("UpdateService","서버 통신 성공")
                            if(response.body()?.data == null)
                                Toast.makeText(baseContext, "기존 비밀번호가 일치하지 않습니다!.", Toast.LENGTH_SHORT).show()
                            else{
                                val builder = AlertDialog.Builder(this@ChangeInfoActivity)
                                builder.setTitle("알림")
                                    .setMessage("회원정보가 정상적으로 수정되었습니다.")
                                    .setPositiveButton("확인",
                                        DialogInterface.OnClickListener{ dialog, id ->
                                            finish()
                                        })
                                builder.show()
                            }
                        }

                        override fun onFailure(call: Call<UserResp>, t: Throwable) {
                            Log.d("updateFail", t.message.toString())
                        }

                    })
                }else{
                    Toast.makeText(baseContext, "변경할 비밀번호와 비밀번호 확인이 일치하지 않습니다!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(baseContext, "모든 내용을 채워주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}