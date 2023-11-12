package com.example.flora

import android.util.Log
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObject {
    val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY//얼만큼 확인할 지
    }
    val client = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
    }.connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS).build()

    private fun getRetrofit(): Retrofit {
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        var result = Retrofit.Builder()
            .baseUrl(BuildConfig.URL_FLOWER)
            .client(client)
            .addConverterFactory(TikXmlConverterFactory.create(parser))
            .build()

        return result
    }

    fun getService(): ApiService {
        return getRetrofit().create(ApiService::class.java)
    }

    private fun getRetrofitServer(): Retrofit {
        var result = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return result
    }
    fun getServerService(): ApiService {
        return getRetrofitServer().create(ApiService::class.java)
    }

    private fun getRetrofitToss(): Retrofit {
        var result = Retrofit.Builder()
            .baseUrl("https://api.tosspayments.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return result
    }
    fun getTossService(): ApiService {
        return getRetrofitToss().create(ApiService::class.java)
    }
}