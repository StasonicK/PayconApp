package com.eburg_soft.payconapp.presentation.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eburg_soft.payconapp.R
import com.eburg_soft.payconapp.databinding.FragmentGoodsBinding
import com.eburg_soft.payconapp.domain.models.GoodModel
import com.eburg_soft.payconapp.presentation.dialogs.showErrorDialog
import com.eburg_soft.payconapp.presentation.extensions.observe
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class GoodsFragment : Fragment(R.layout.fragment_goods), DIAware {

    override val di by closestDI()
    private var _binding: FragmentGoodsBinding? = null
    private val binding get() = _binding!!

    private val viewModelFactory: GoodsViewModelFactory by instance()
    private val viewModel: GoodsViewModel by lazy {
        ViewModelProvider(this,viewModelFactory).get(GoodsViewModel::class.java)
    }
    private var dialog: ProgressDialog? = null
    private val adapter = GoodsAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGoodsBinding.bind(view)
        setupUi()
        observerLiveData()
    }

    private fun setupUi() {
        with(binding) {
            goodsRecycler.adapter = adapter
            goodsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            goodsRecycler.setHasFixedSize(true)

            loadFromApiBtn.setOnClickListener { viewModel.getGoodsFromApi() }
            loadFromFileBtn.setOnClickListener { }
        }
    }

    private fun observerLiveData() {
        observe(viewModel.isLoadingLiveData) { showLoading(it) }
        observe(viewModel.errorMessageLiveData) { showErrorDialog(it) }
        observe(viewModel.goodsLiveData) { showGoods(it) }
    }

    private fun showGoods(goods: List<GoodModel>) {
        adapter.submit(goods)
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            dialog = ProgressDialog(requireContext())
            dialog!!.setMessage(requireContext().getString(R.string.progress_loading_date))
            dialog!!.isIndeterminate = true
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.show()
        } else dialog = null
        val iii = 1
    }

    companion object {

        fun newInstance() = GoodsFragment()
    }
}