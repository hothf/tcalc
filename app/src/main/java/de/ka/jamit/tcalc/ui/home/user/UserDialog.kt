package de.ka.jamit.tcalc.ui.home.user

import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogUserBinding

/**
 * A bottom sheet for adding a new value.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class UserDialog : BaseDialogFragment<DialogUserBinding, UserDialogViewModel>(
        R.layout.dialog_user,
        UserDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
) {

    override fun onHandle(element: Any?) {
        if (element is UserDialogViewModel.Close) {
            dismissAllowingStateLoss()
        } else if (element is UserDialogViewModel.ShowDialogSnack) {
            getBinding<DialogUserBinding>()?.dialogSnacker?.reveal(element.snack)
        }
    }
}

