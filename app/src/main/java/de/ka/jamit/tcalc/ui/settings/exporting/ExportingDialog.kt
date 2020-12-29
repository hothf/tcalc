package de.ka.jamit.tcalc.ui.settings.exporting

import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseDialogFragment
import de.ka.jamit.tcalc.databinding.DialogExportingBinding

/**
 * A dialog for importing records.
 *
 * Created by Thomas Hofmann on 09.07.20
 **/
// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseExportDialogFragment : BaseDialogFragment<DialogExportingBinding, ExportingDialogViewModel>(
        R.layout.dialog_exporting,
        ExportingDialogViewModel::class,
        DialogMode.DIALOG,
        cancellable = false
)

@AndroidEntryPoint
class ExportingDialog : BaseExportDialogFragment() {

    override fun onHandle(element: Any?) {
        if (element is ExportingDialogViewModel.Completed) {
            element.uri?.let {
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, it)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.share_export)))
            }
            dismissAllowingStateLoss()
        }
    }

    override fun onArgumentsReceived(bundle: Bundle) {
        val uri = bundle.getString(URI_KEY)?.toUri()
        viewModel.export(uri)
    }

    companion object {
        const val URI_KEY = "_k_uri"
    }
}