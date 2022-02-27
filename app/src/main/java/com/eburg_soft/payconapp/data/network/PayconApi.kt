package com.eburg_soft.payconapp.data.network

import com.eburg_soft.payconapp.data.models.ItemResponse
import io.reactivex.Single
import retrofit2.http.GET

interface PayconApi {

    @GET("/api1.php")
    fun getItems1(): Single<List<ItemResponse>>

    @GET("/api2.php")
    fun getItems2(): Single<List<ItemResponse>>
}