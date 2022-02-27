package com.eburg_soft.payconapp.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.eburg_soft.payconapp.R
import com.eburg_soft.payconapp.presentation.base.arguments
import com.eburg_soft.payconapp.presentation.base.withArgs
import com.eburg_soft.payconapp.presentation.dialogs.ErrorDialogFragment.Args
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.parcelize.Parcelize

class ErrorDialogFragment : DialogFragment() {

    private val args by arguments<Args>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext()).let {
            it.setTitle(R.string.error_title)
            it.setMessage(args.message)
            it.show()
        }

    @Parcelize
    class Args(val message: String) : Parcelable
}

fun Fragment.showErrorDialog(message: String) {
    val args = Args(message)
    val dialog = ErrorDialogFragment().withArgs(args)
    dialog.show(childFragmentManager, "error_dialog")
}