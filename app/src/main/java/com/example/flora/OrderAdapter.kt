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
import com.example.flora.databinding.ActivityShopDetailBinding
import com.example.flora.databinding.ShopBagItemBinding
import com.example.flora.databinding.ShopDetailItemBinding
import retrofit2.Call
import retrofit2.Response
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class OrderAdapter(var order: ActivityShopDetailBinding) : RecyclerView.Adapter<OrderHolder>() {
    var listdata = mutableListOf<OrderData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val binding = ShopDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        val item = listdata.get(position)
        holder.setFlower(item)
    }

    override fun getItemCount(): Int {
        return listdata.size
    }
}

class OrderHolder(val binding: ShopDetailItemBinding) : RecyclerView.ViewHolder(binding.root) {
    init {

    }

    fun setFlower(item: OrderData) {
        binding.flowerName.setText(item.flowerName)
        binding.money.setText(item.price.toInt().toString())


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