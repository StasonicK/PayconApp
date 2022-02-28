package com.eburg_soft.payconapp.presentation.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
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
    private val getFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            it.data?.data?.path?.let { path ->
                viewModel.showGoodsFromFile(path
//                    .substring(path.lastIndexOf("/") + 1)
                )
            }
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.savedInstanceState = savedInstanceState

        savedInstanceState?.let {
            val fragmentState = it.getBundle(FRAGMENT_STATE) ?: return@let
            val items = fragmentState.getParcelableArrayList(ITEMS) ?: emptyList<GoodModel>()
            adapter.submit(items)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(FRAGMENT_STATE, bundleOf(ITEMS to adapter.items))
        super.onSaveInstanceState(outState)
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

            loadFromApiBtn.setOnClickListener { viewModel.showGoodsFromApi() }
            loadFromFileBtn.setOnClickListener { openFile() }
        }
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "file"
        intent.type = "text/csv"
//        intent.type = "file"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // special intent for Samsung file manager
        val sIntent = Intent("com.sec.android.app.myfiles.PICK_DATA")

        // if you want any file type, you can skip next line
//        sIntent.putExtra("CONTENT_TYPE", "file")
        sIntent.putExtra("CONTENT_TYPE", "text/csv")
        sIntent.addCategory(Intent.CATEGORY_DEFAULT)

        val chooserIntent: Intent
        if (sIntent.resolveActivity(requireContext().packageManager) != null) {
            // it is device with Samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Открыть файл")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intent))
        } else {
            chooserIntent = Intent.createChooser(intent, "Открыть файл")
        }

        try {
//            chooserIntent.type = "file"
            getFileResult.launch(chooserIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "Не найден подходящий файловый менеджер",
                Toast.LENGTH_SHORT
            ).show()
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
        private const val FRAGMENT_STATE = "fragment_state"
        private const val KEY_LAST_ITEM_POSITION = "key_last_item_position"
        private const val ITEMS = "key_last_item_position"
        private const val PICKFILE_REQUEST_CODE = 777999
    }
}