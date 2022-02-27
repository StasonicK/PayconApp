package com.eburg_soft.payconapp.presentation.fragments

import androidx.recyclerview.widget.DiffUtil
import com.eburg_soft.payconapp.domain.models.GoodModel

class GoodsDiffUtilCallback(
    private val oldList: List<GoodModel>,
    private val newList: List<GoodModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}