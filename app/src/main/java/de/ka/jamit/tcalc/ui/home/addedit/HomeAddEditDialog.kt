package de.ka.jamit.tcalc.ui.home.addedit

import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogHomeAddeditBinding

/**
 * A bottom sheet for adding a new value.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeAddEditDialog : BaseDialogFragment<DialogHomeAddeditBinding, HomeAddEditDialogViewModel>(
        R.layout.dialog_home_addedit,
        HomeAddEditDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
) {

    override fun onHandle(element: Any?) {
        if (element is HomeAddEditDialogViewModel.Choose) {
            dismissAllowingStateLoss()
        }
    }

    companion object {
        const val TITLE_KEY = "_k_title_"
        const val ID_KEY = "_k_id_"
        const val VALUE_KEY = "_k_val_"
        const val TIMESPAN_KEY = "_k_timespan_"
        const val CATEGORY_KEY = "_k_category"
        const val UPDATE_KEY = "_k_isupdate_"
    }
}

