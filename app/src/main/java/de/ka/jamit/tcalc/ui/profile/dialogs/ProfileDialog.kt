package de.ka.jamit.tcalc.ui.profile.dialogs

import android.os.Bundle
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogProfileBinding
import timber.log.Timber

/**
 * An example profile dialog
 * Created by Thomas Hofmann on 23.04.20
 **/
class ProfileDialog : BaseDialogFragment<DialogProfileBinding, ProfileDialogViewModel>(
    R.layout.dialog_profile,
    ProfileDialogViewModel::class,
    cancellable = true // set to false to not close dialog on back press or click outside
) {

    override fun onConsumeBackPress(): Boolean {
        Timber.e("See how this is consumed")
        return false // set true if intercepting the back press is wanted + do stuff here, remove method otherwise
    }

    override fun onHandle(element: Any?) {
        if (element is ProfileDialogViewModel.Choose) {

            val resultBundle = Bundle().apply {
                putInt(DIALOG_CHOOSE_RESULT, element.value)
            }
            dismissDialogWithResult("dialogResult", resultBundle)
        }


    }

    companion object {
        const val DIALOG_CHOOSE_RESULT = "choose_result"
    }
}