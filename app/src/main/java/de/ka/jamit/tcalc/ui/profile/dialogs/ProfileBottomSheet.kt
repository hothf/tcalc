package de.ka.jamit.tcalc.ui.profile.dialogs

import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogProfileBinding

/**
 * An example profile bottom sheet
 * Created by Thomas Hofmann on 23.04.20
 **/
class ProfileBottomSheet : BaseDialogFragment<DialogProfileBinding, ProfileDialogViewModel>(
    R.layout.dialog_profile,
    ProfileDialogViewModel::class,
    DialogMode.BOTTOM_SHEET,
    cancellable = true // set to false to not close dialog on back press or click outside
) {

    override fun onConsumeBackPress(): Boolean {
        return false // set true if intercepting the back press is wanted + do stuff here, remove method otherwise
    }
}