package de.ka.jamit.tcalc.ui.settings.importing

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogImportingBinding

/**
 * A dialog for importing records.
 *
 * Created by Thomas Hofmann on 09.07.20
 **/
// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseImportDialogFragment : BaseDialogFragment<DialogImportingBinding, ImportingDialogViewModel>(
        R.layout.dialog_importing,
        ImportingDialogViewModel::class,
        DialogMode.DIALOG,
        cancellable = false
)

@AndroidEntryPoint
class ImportingDialog : BaseImportDialogFragment() {

    override fun onHandle(element: Any?) {
        if (element is ImportingDialogViewModel.Completed) {
            if (element.lastImportResult != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissAllowingStateLoss()
                }, SUCCESS_DIsMISS_DELAY_MS)
            } else {
                dismissAllowingStateLoss()
            }
        }
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        val uri = bundle.getString(URI_KEY)?.toUri()
        viewModel.import(uri)
    }

    companion object {
        const val URI_KEY = "_k_uri"
        const val SUCCESS_DIsMISS_DELAY_MS = 4000L
    }
}

