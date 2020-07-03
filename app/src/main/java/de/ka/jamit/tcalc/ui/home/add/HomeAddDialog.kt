package de.ka.jamit.tcalc.ui.home.add

import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogHomeAddBinding

/**
 * A bottom sheet for adding a new value.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeAddDialog : BaseDialogFragment<DialogHomeAddBinding, HomeAddDialogViewModel>(
        R.layout.dialog_home_add,
        HomeAddDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
) {

    override fun onHandle(element: Any?) {
        if (element is HomeAddDialogViewModel.Choose) {
            dismissAllowingStateLoss()
        }
    }
}

