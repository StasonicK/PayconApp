package com.eburg_soft.payconapp.presentation.screens.viewModels.goods

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eburg_soft.payconapp.R
import com.eburg_soft.payconapp.data.NetworkUtils
import com.eburg_soft.payconapp.domain.models.GoodModel
import com.eburg_soft.payconapp.domain.repositories.PayconRepository
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File

class GoodsViewModel(private val payconRepository: PayconRepository) : ViewModel() {

    private val goodsMutableLiveData = MutableLiveData<List<GoodModel>>()
    val goodsLiveData get() = goodsMutableLiveData
    private val isLoadingMutableLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData get() = isLoadingMutableLiveData

    //    private val isErrorMutableLiveData = MutableLiveData<Boolean>()
//    val isErrorLiveData get() = isLoadingMutableLiveData
    private val errorMessageMutableLiveData = MutableLiveData<String>()
    val errorMessageLiveData get() = errorMessageMutableLiveData

    fun showGoodsFromApi(context: Context) {
        isLoadingMutableLiveData.value = true
        errorMessageMutableLiveData.value = null

        if (!NetworkUtils.isNetworkAvailable(context)) {
            isLoadingMutableLiveData.value = false
            errorMessageMutableLiveData.value = context.getString(R.string.error_network_unavailable)
            return
        }

        payconRepository.getFromApiAllGoods()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                isLoadingMutableLiveData.value = false
                errorMessageMutableLiveData.value = it.message
            }
            .doOnSuccess { list: List<GoodModel> ->
                goodsMutableLiveData.value = list
                isLoadingMutableLiveData.value = false
            }
            .subscribe()
    }

    fun showGoodsFromFile(file: File) {
        isLoadingMutableLiveData.value = true
        errorMessageMutableLiveData.value = null

        val csvReader = CsvReader()
        val list = mutableListOf<GoodModel>()

        csvReader.open(file) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                val name = row["Title"] ?: return@forEach
                val priceString = row["Price"] ?: return@forEach
                val price = priceRegex.replace(priceString, "").toDoubleOrNull() ?: return@forEach
                val goodModel = GoodModel(0, name, price)
                list.add(goodModel)
            }
        }

        goodsMutableLiveData.value = list
        isLoadingMutableLiveData.value = false
    }

    companion object {

        private val priceRegex = Regex("[^0-9,.]")
    }
}