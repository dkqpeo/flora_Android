package com.example.flora

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object MySharedPreferences {
    // 로그인 비로그인 상태 저장 객체
    private val LOGIN_STATUS: String = "status"
    /*private val USER_SEQ: Int = 0
    private val USER_NAME: String = "userName"
    private val USER_NAME: String = "userName"
    private val USER_NAME: String = "userName"
    private val USER_NAME: String = "userName"*/
    // 로그인 사용자의 정보를 담아둠.
    private val LOGIN_INFO: String = "my_freferences"

    // 로그인 했을 경우 사용
    fun setLogin(context: Context, input: String, data: UserData){
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_INFO, 0)
        val editor: SharedPreferences.Editor = prefs.edit()
        val prefs1: SharedPreferences = context.getSharedPreferences(LOGIN_STATUS, 0)
        val editor1: SharedPreferences.Editor = prefs1.edit()

        editor1.putString("status", input)
        editor1.commit()

        editor.putLong("seq", data.seq.toLong())
        editor.putString("userId", data.userId.toString())
        editor.putString("userName", data.userName.toString())
        editor.putString("tel", data.tel.toString())
        editor.putString("email", data.email.toString())
        editor.commit()
        Log.d("email", data.email.toString())
    }

    // 로그인 사용자의 정보를 요청할 때 사용
    fun getLogin(context: Context): MutableList<UserData>{
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_INFO, 0)
        var result: MutableList<UserData> = mutableListOf()

        result.clear()

        var seq: Long = prefs.getLong("seq", 0).toLong()
        var id: String = prefs.getString("userId", "").toString()
        var name: String = prefs.getString("userName", "").toString()
        var tel: String = prefs.getString("tel", "").toString()
        var email: String = prefs.getString("email", "").toString()
        var data = UserData(seq, id, name, tel, email)
        result.add(data)

        Log.d("getEmail", email)
        return result
    }

    // 로그인 비로그인 여부 확인
    fun getLoginStatus(context: Context): String? {
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_STATUS, 0)
        return prefs.getString("status", "")
    }

    // 로그아웃 할 경우 저장된 정보를 모두 지운다.
    fun setLogout(context: Context){
        val prefs: SharedPreferences = context.getSharedPreferences(LOGIN_INFO, 0)
        val editor: SharedPreferences.Editor = prefs.edit()
        val prefs1: SharedPreferences = context.getSharedPreferences(LOGIN_STATUS, 0)
        val editor1: SharedPreferences.Editor = prefs1.edit()
        editor.clear()
        editor.commit()
        editor1.clear()
        editor1.commit()
    }
}