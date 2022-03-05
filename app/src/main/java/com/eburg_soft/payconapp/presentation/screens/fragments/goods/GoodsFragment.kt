package com.eburg_soft.payconapp.presentation.screens.fragments.goods

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore.Audio
import android.provider.MediaStore.Images.Media
import android.provider.MediaStore.Video
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eburg_soft.payconapp.R
import com.eburg_soft.payconapp.databinding.FragmentGoodsBinding
import com.eburg_soft.payconapp.domain.models.GoodModel
import com.eburg_soft.payconapp.presentation.dialogs.showErrorDialog
import com.eburg_soft.payconapp.presentation.extensions.observe
import com.eburg_soft.payconapp.presentation.screens.viewModels.goods.GoodsViewModel
import com.eburg_soft.payconapp.presentation.screens.viewModels.goods.GoodsViewModelFactory
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import java.io.File

class GoodsFragment : Fragment(R.layout.fragment_goods), DIAware, ActivityCompat.OnRequestPermissionsResultCallback {

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
    private var isAbleReadFiles = false

    @SuppressLint("MissingPermission")
    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                isAbleReadFiles = true
            } else {
                isAbleReadFiles = true
            }
        }
    private val getFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            it.data?.data?.let { uri ->
                val path = getPath(uri) ?: return@let

                when (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )) {
                    PackageManager.PERMISSION_GRANTED -> viewModel.showGoodsFromFile(File(path))
                    else -> requestReadExternalStoragePermission()
                }
            }
        }

    private fun requestReadExternalStoragePermission() {
//        registerForActivityResult()
//        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),200)
    }

    private fun getPath(uri: Uri): String? {

        val isKitKat = VERSION.SDK_INT >= VERSION_CODES.KITKAT

        // DocumentProvider

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(requireContext(), uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(contentUri!!, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path!!
        }

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(
        uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )

        try {
            cursor = requireContext().contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
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
        getPermission()
    }

    private fun setupUi() {
        with(binding) {
            goodsRecycler.adapter = adapter
            goodsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            goodsRecycler.setHasFixedSize(true)

            loadFromApiBtn.setOnClickListener { viewModel.showGoodsFromApi(requireContext()) }
            loadFromFileBtn.setOnClickListener { openFile() }
        }
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/csv"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // special intent for Samsung file manager
        val sIntent = Intent("com.sec.android.app.myfiles.PICK_DATA")

        // if you want any file type, you can skip next line
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
            if (isAbleReadFiles) getFileResult.launch(chooserIntent)
            else showErrorDialog("Нет разрешения на чтение файлов")
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

    private fun getPermission() {
        permissionRequest.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
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