package com.example.flora

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flora.databinding.ActivityListBinding
import com.example.flora.databinding.ActivityListItemBinding
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CustomAdapter : RecyclerView.Adapter<Holder>() {
    var listdata = mutableListOf<Flist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ActivityListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val flower = listdata.get(position)
        holder.setFlower(flower)
    }

    override fun getItemCount(): Int {
        return listdata.size
    }
}

class Holder(val binding: ActivityListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            val detail = Intent(binding.root.context, DetailActivity::class.java)
            detail.putExtra("dataNo", binding.dataNo.text.toString())
            binding.root.context.startActivity(detail)
            //Toast.makeText(binding.root.context, binding.dataNo.text, Toast.LENGTH_SHORT).show()
        }
    }

    fun setFlower(flower: Flist) {
        binding.dataNo.setText(flower.dataNo)
        binding.listName.setText(flower.name)
        binding.listLang.setText(flower.lang)

        disableSslVerification()
        // 이미지 가져오기
        Glide.with(binding.root.context)
            .load(flower.file1)
            .into(binding.listImg)
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