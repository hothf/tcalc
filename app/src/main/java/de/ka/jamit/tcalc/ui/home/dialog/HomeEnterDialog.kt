package de.ka.jamit.tcalc.ui.home.dialog

import android.os.Bundle
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogHomeEnterBinding

/**
 * A bottom sheet for entering a value.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeEnterDialog : BaseDialogFragment<DialogHomeEnterBinding, HomeEnterDialogViewModel>(
        R.layout.dialog_home_enter,
        HomeEnterDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
) {

    override fun onHandle(element: Any?) {
        dismissAllowingStateLoss()
    }

    companion object {
        const val TITLE_KEY = "_k_title_"
        const val RESULT_KEY = "_k_result"
        const val VALUE_KEY = "_k_val"
    }
}

