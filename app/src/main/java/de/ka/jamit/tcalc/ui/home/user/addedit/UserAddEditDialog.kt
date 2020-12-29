package de.ka.jamit.tcalc.ui.home.user.addedit

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogHomeAddeditBinding

/**
 * A bottom sheet for adding or updating a user.
 *
 * Created by Thomas Hofmann on 08.07.20
 **/
// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseUserAddEditDialogFragment : BaseDialogFragment<DialogHomeAddeditBinding, UserAddEditDialogViewModel>(
        R.layout.dialog_user_addedit,
        UserAddEditDialogViewModel::class,
        DialogMode.BOTTOM_SHEET,
        cancellable = true
)

@AndroidEntryPoint
class UserAddEditDialog : BaseUserAddEditDialogFragment() {

    override fun onHandle(element: Any?) {
        if (element is UserAddEditDialogViewModel.Choose) {
            dismissAllowingStateLoss()
        }
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        val id = bundle.getInt(ID_KEY)
        val updating = bundle.getBoolean(UPDATE_KEY, false)
        val title = bundle.getString(TITLE_KEY, "")

        viewModel.updateWith(id, updating, title)
    }

    companion object {
        const val TITLE_KEY = "_k_title_"
        const val ID_KEY = "_k_id_"
        const val UPDATE_KEY = "_k_isupdate_"
    }
}

