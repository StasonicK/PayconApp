package com.eburg_soft.payconapp.domain.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GoodModel(
    val id: Int,
    val name: String,
    val price: Double
) : Parcelable
