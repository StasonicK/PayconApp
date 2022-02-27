package com.eburg_soft.payconapp.presentation.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eburg_soft.payconapp.domain.repositories.PayconRepository

class GoodsViewModelFactory(private val payconRepository: PayconRepository)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GoodsViewModel(payconRepository) as T
    }
}