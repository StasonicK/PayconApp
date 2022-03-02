package com.eburg_soft.payconapp.presentation.screens.fragments.goods

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eburg_soft.payconapp.R
import com.eburg_soft.payconapp.databinding.ItemGoodBinding
import com.eburg_soft.payconapp.domain.models.GoodModel
import com.eburg_soft.payconapp.presentation.screens.fragments.goods.GoodsAdapter.GoodViewHolder

class GoodsAdapter() : RecyclerView.Adapter<GoodViewHolder>() {

    val items = mutableListOf<GoodModel>()

    public fun submit(newItems: List<GoodModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodViewHolder {
        val itemBinding =
            ItemGoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoodViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: GoodViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class GoodViewHolder(private val itemBinding: ItemGoodBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: GoodModel) {
            with(itemBinding) {
                name.text = item.name
                price.text = "${item.price} ${R.string.sign_of_ruble}"
            }
        }
    }
}