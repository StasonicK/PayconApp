package com.eburg_soft.payconapp.domain.repositories

import com.eburg_soft.payconapp.domain.models.GoodModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface PayconRepository {

    fun getFromApiAllGoods(): Single<List<GoodModel>>
}