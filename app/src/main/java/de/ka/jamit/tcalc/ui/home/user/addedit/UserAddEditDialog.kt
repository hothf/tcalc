package de.ka.jamit.tcalc.ui.home.user.addedit

import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogHomeAddeditBinding

/**
 * A bottom sheet for adding or updating a user.
 *
 * Created by Thomas Hofmann on 08.07.20
 **/
class UserAddEditDialog : BaseDialogFragment<DialogHomeAddeditBinding, UserAddEditDialogViewModel>(
        R.layout.dialog_user_addedit,
        UserAddEditDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
) {

    override fun onHandle(element: Any?) {
        if (element is UserAddEditDialogViewModel.Choose) {
            dismissAllowingStateLoss()
        }
    }

    companion object {
        const val TITLE_KEY = "_k_title_"
        const val ID_KEY = "_k_id_"
        const val UPDATE_KEY = "_k_isupdate_"
    }
}

