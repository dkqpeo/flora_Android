package com.example.flora

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flora.databinding.ActivityShopDetailBinding
import retrofit2.Call
import retrofit2.Response

class OrderDetail : AppCompatActivity() {
    val binding by lazy { ActivityShopDetailBinding.inflate(layoutInflater) }
    var result: MutableList<OrderData> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // MySharedPreferences에 저장된 사용자 이름 가져오기
        var userInfo: MutableList<UserData> = mutableListOf()
        userInfo = MySharedPreferences.getLogin(this)
        var userName = userInfo.get(0).userName

        if (userName != null){
            // 사용자의 장바구니 목록 가져오기
            RetrofitObject.getServerService().orderList(userName)
                .enqueue(object : retrofit2.Callback<OrderListResp>{
                    override fun onResponse(call: Call<OrderListResp>, response: Response<OrderListResp>) {
                        setOrderList(response.body())
                    }

                    override fun onFailure(call: Call<OrderListResp>, t: Throwable) {
                        Log.d("fail", t.message.toString())
                    }
                })
        }
    }

    private fun setOrderList(body: OrderListResp?){
        result.clear()
        if(body != null)
            for (i in body.data){
                var seq = i.seq.toLong()
                var date = i.date
                var flowerName = i.flowerName.toString()
                var price = i.price.toInt()
                var flowerImg = i.flowerImg.toString()
                var postNum = i.postNum.toString()
                var address = i.address.toString()
                var detailAddress = i.detailAddress.toString()
                var fromUser = i.fromUser.toString()
                var fromUserTel = i.fromUserTel.toString()
                var status = i.status.toInt()
                var userSeq = i.userSeq
                var orderList = OrderData(seq, date, flowerName, price, flowerImg, postNum, address, detailAddress, fromUser, fromUserTel, status, userSeq)
                result.add(orderList)
            }
        createApapter()
    }

    private fun createApapter() {
        var adapter = OrderAdapter(binding)

        adapter.listdata = result
        binding.recyleView.adapter = adapter
        binding.recyleView.layoutManager = LinearLayoutManager(this)

    }
}