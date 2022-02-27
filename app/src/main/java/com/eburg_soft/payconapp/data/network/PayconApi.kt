package com.eburg_soft.payconapp.data.network

import com.eburg_soft.payconapp.data.models.GoodResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface PayconApi {

    @GET("/api1.php")
    fun getGoods1(): Single<List<GoodResponse>>

    @GET("/api2.php")
    fun getGoods2(): Single<List<GoodResponse>>

    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): PayconApi {
            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl("https://paycon.us/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PayconApi::class.java)
        }
    }
}