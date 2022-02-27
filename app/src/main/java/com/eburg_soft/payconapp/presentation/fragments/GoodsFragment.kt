package com.eburg_soft.payconapp.presentation.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
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
        ViewModelProvider(this, viewModelFactory).get(GoodsViewModel::class.java)
    }
    private var dialog: ProgressDialog? = null
    private val adapter = GoodsAdapter()

    private var savedInstanceState: Bundle? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.savedInstanceState = savedInstanceState
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
        restorePreviousUIState()
        animateRecyclerViewOnlyInTheBeginning()
    }

    private fun restorePreviousUIState() {
        savedInstanceState?.let {
            val lastItemPosition = it.getInt(KEY_LAST_ITEM_POSITION)
            binding.goodsRecycler.scrollToPosition(lastItemPosition)
        }
    }

    private fun animateRecyclerViewOnlyInTheBeginning() {
        if (getLastItemPosition() == 0) {
            binding.goodsRecycler.layoutAnimation =
                AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down)
        }
    }

    private fun getLastItemPosition(): Int {
        var lastItemPosition = 0

        savedInstanceState?.let {
            if (it.containsKey(KEY_LAST_ITEM_POSITION)) {
                lastItemPosition = it.getInt(KEY_LAST_ITEM_POSITION)
            }
        }
        return lastItemPosition
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            dialog = ProgressDialog(requireContext())
            dialog!!.setTitle(getString(R.string.progress_loading_date));
            dialog!!.setCancelable(false);
            dialog!!.isIndeterminate = true;
            dialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog!!.show();
        } else {
            dialog?.dismiss()
        }
    }

    companion object {

        fun newInstance() = GoodsFragment()
        private const val KEY_LAST_ITEM_POSITION = "KEY_LAST_ITEM_POSITION"
    }
}