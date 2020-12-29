package de.ka.jamit.tcalc.ui.home.user

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.base.events.NavigateTo
import de.ka.jamit.tcalc.base.navigate
import de.ka.jamit.tcalc.databinding.DialogUserBinding
import de.ka.jamit.tcalc.ui.home.addedit.HomeAddEditDialog
import de.ka.jamit.tcalc.ui.home.user.addedit.UserAddEditDialog

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
        when (element) {
            is UserDialogViewModel.Close -> {
                dismissAllowingStateLoss()
            }
            is UserDialogViewModel.ShowDialogSnack -> {
                getBinding<DialogUserBinding>()?.dialogSnacker?.reveal(element.snack)
            }
            is UserDialogViewModel.Add -> {
                navigate(NavigateTo(R.id.dialogUserAddEdit))
            }
            is UserDialogViewModel.Edit -> {
                element.item?.let { user ->
                    val arguments = Bundle().apply {
                        putBoolean(UserAddEditDialog.UPDATE_KEY, true)
                        putString(UserAddEditDialog.TITLE_KEY, user.name)
                        putInt(HomeAddEditDialog.ID_KEY, user.id)
                    }
                    navigate(NavigateTo(R.id.dialogUserAddEdit, args = arguments))
                }
            }
        }
    }
}

