package de.ka.jamit.tcalc.ui.settings.importing

import android.os.Handler
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogImportingBinding

/**
 * A dialog for importing records.
 *
 * Created by Thomas Hofmann on 09.07.20
 **/
class ImportingDialog : BaseDialogFragment<DialogImportingBinding, ImportingDialogViewModel>(
        R.layout.dialog_importing,
        ImportingDialogViewModel::class,
        DialogMode.DIALOG,
        cancellable = false
) {

    override fun onHandle(element: Any?) {
        if (element is ImportingDialogViewModel.Completed) {
            if (element.lastImportResult != null) {
                Handler().postDelayed({
                    dismissAllowingStateLoss()
                }, SUCCESS_DIsMISS_DELAY_MS)
            } else {
                dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        const val URI_KEY = "_k_uri"
        const val SUCCESS_DIsMISS_DELAY_MS = 4000L
    }
}

