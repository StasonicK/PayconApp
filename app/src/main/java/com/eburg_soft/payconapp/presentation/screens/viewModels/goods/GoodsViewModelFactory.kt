package com.eburg_soft.payconapp.presentation.screens.viewModels.goods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eburg_soft.payconapp.data.NetworkUtils
import com.eburg_soft.payconapp.domain.repositories.PayconRepository

class GoodsViewModelFactory(private val payconRepository: PayconRepository, private val networkUtils: NetworkUtils) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GoodsViewModel(payconRepository, networkUtils) as T
    }
}