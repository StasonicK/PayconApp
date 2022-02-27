package com.eburg_soft.payconapp.data.repositories

import com.eburg_soft.payconapp.data.models.GoodResponse
import com.eburg_soft.payconapp.data.network.PayconApi
import com.eburg_soft.payconapp.domain.models.GoodModel
import com.eburg_soft.payconapp.domain.repositories.PayconRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class PayconRepositoryImpl(private val payconApi: PayconApi) : PayconRepository {

    override fun getFromApiAllGoods(): Single<List<GoodModel>> {
        return Single.zip(
            payconApi.getGoods1(),
            payconApi.getGoods2()
        ) { list1, list2 ->
            val list = ArrayList<GoodResponse>()
            list.apply {
                addAll(list1)
                addAll(list2)
            }
            mapGoods(list)
        }.subscribeOn(Schedulers.io())
    }

    private companion object {

        fun mapGoods(responses: List<GoodResponse>): List<GoodModel> =
            responses.map { GoodModel(id = it.id, name = it.name, price = it.price) }
    }
}