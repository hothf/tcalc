package de.ka.jamit.tcalc.ui.home.user

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.setFragmentResult
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
        if (element is UserDialogViewModel.Choose) {
            setFragmentResult(FRAGMENT_RESULT_KEY, Bundle())
        } else if (element is UserDialogViewModel.UserAddEdit) {
            setFragmentResult(FRAGMENT_RESULT_KEY, element.args)
        }
    }

    companion object {
        const val FRAGMENT_RESULT_KEY = "usr_d_key"
    }
}

