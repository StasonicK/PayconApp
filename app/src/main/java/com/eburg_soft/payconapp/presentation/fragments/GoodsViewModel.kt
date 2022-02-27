package com.eburg_soft.payconapp.presentation.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eburg_soft.payconapp.domain.models.GoodModel
import com.eburg_soft.payconapp.domain.repositories.PayconRepository
import io.reactivex.android.schedulers.AndroidSchedulers

class GoodsViewModel(private val payconRepository: PayconRepository) : ViewModel() {

    private val goodsMutableLiveData = MutableLiveData<List<GoodModel>>()
    val goodsLiveData get() = goodsMutableLiveData
    private val isLoadingMutableLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData get() = isLoadingMutableLiveData

    //    private val isErrorMutableLiveData = MutableLiveData<Boolean>()
//    val isErrorLiveData get() = isLoadingMutableLiveData
    private val errorMessageMutableLiveData = MutableLiveData<String>()
    val errorMessageLiveData get() = errorMessageMutableLiveData

    fun getGoodsFromApi() {
        isLoadingMutableLiveData.value = true
//        isErrorMutableLiveData.value = false
        errorMessageMutableLiveData.value = null

        payconRepository.getFromApiAllGoods()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                isLoadingMutableLiveData.value = false
                errorMessageMutableLiveData.value = it.message
//                isErrorMutableLiveData.value = true
            }
            .doOnSuccess { list: List<GoodModel> ->
                goodsMutableLiveData.value = list
                isLoadingMutableLiveData.value = false
            }
            .subscribe()
    }

    fun getGoodsFromFile() {
        //TODO did it!
    }
}