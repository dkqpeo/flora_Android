package com.example.flora

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flora.databinding.ActivityShopBagBinding
import com.example.flora.databinding.ShopBagItemBinding
import retrofit2.Call
import retrofit2.Response
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CartAdapter(var cart: ActivityShopBagBinding) : RecyclerView.Adapter<CartHolder>() {
    var listdata = mutableListOf<CartData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val binding = ShopBagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartHolder(binding, cart)
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val item = listdata.get(position)
        holder.setFlower(item)
    }

    override fun getItemCount(): Int {
        return listdata.size
    }
}

var total: Int = 0
var selectItem = mutableListOf<Long>()

class CartHolder(val binding: ShopBagItemBinding, val cart: ActivityShopBagBinding) : RecyclerView.ViewHolder(binding.root) {
    init {
        // 체크박스 선택 시
        binding.check.setOnClickListener{
            // 체크박스 선택시
            if(binding.check.isChecked){
                total += binding.price.text.toString().toInt()
                cart.totalPrice.setText(total.toString())

                var seq = binding.itemSeq.text.toString().toLong()

                selectItem.add(seq)

            }
            else{ // 체크박스 선택 해제시
                total -= binding.price.text.toString().toInt()
                cart.totalPrice.setText(total.toString())

                var seq = binding.itemSeq.text.toString().toLong()

                selectItem.remove(seq)
            }
        }

        // 삭제 버튼 클릭 시
        binding.delete.setOnClickListener {
            var seq = binding.itemSeq.text.toString().toLong()

            // seq값을 넘겨줌
            RetrofitObject.getServerService().deleteItem(seq)
                .enqueue(object : retrofit2.Callback<CartDelResp> {
                    override fun onResponse(
                        call: Call<CartDelResp>,
                        response: Response<CartDelResp>
                    ) {
                        // 삭제 성공했을 경우
                        val intent = Intent(cart.recyleView?.context, ShopBagActivity::class.java)
                        ContextCompat.startActivity(cart.recyleView.context, intent, null)
                    }
                    override fun onFailure(call: Call<CartDelResp>, t: Throwable) {
                        Log.d("fail", t.message.toString())
                    }
                })
        }


    }

    fun setFlower(item: CartData) {
        binding.flowerName.setText(item.flowerName)
        binding.itemSeq.setText(item.seq.toString())
        binding.price.setText(item.price.toInt().toString())

        disableSslVerification()
        // 이미지 가져오기
        Glide.with(binding.root.context)
            .load(item.flowerImg)
            .into(binding.flowerImg)
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