package com.eburg_soft.payconapp.presentation.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

fun <T : Any?> Fragment.observe(liveData: LiveData<T>?, observer: (T) -> Unit) {
    liveData?.observe(viewLifecycleOwner, {
        it?.let(observer)
    })
}