package de.ka.jamit.tcalc.ui.home.user

import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogUserBinding

/**
 * A bottom sheet for adding a new value.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseUserDialogFragment : BaseDialogFragment<DialogUserBinding, UserDialogViewModel>(
        R.layout.dialog_user,
        UserDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
)

@AndroidEntryPoint
class UserDialog : BaseUserDialogFragment() {

    override fun onHandle(element: Any?) {
        if (element is UserDialogViewModel.Close) {
            dismissAllowingStateLoss()
        } else if (element is UserDialogViewModel.ShowDialogSnack) {
            getBinding<DialogUserBinding>()?.dialogSnacker?.reveal(element.snack)
        }
    }
}

