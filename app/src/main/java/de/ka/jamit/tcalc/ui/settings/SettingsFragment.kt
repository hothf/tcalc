package de.ka.jamit.tcalc.ui.settings

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseFragment
import de.ka.jamit.tcalc.base.events.NavigateTo
import de.ka.jamit.tcalc.base.navigate
import de.ka.jamit.tcalc.databinding.FragmentSettingsBinding
import de.ka.jamit.tcalc.ui.settings.exporting.ExportingDialog
import de.ka.jamit.tcalc.ui.settings.importing.ImportingDialog
import timber.log.Timber


class SettingsFragment :
        BaseFragment<FragmentSettingsBinding, SettingsViewModel>(R.layout.fragment_settings, SettingsViewModel::class) {


    override fun onHandle(element: Any?) {
        super.onHandle(element)
        if (element is SettingsViewModel.Import) {
            import()
        } else if (element is SettingsViewModel.Export) {
            export()
        }
    }


    private val importIntent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Timber.e("!!! uri import :$uri")
        // TODO parse file from uri  and persist it in db if right format and anything
        uri?.let {
            val arguments = Bundle().apply {
                putString(ImportingDialog.URI_KEY, uri.toString())
            }
            navigate(NavigateTo(R.id.dialogImport, args = arguments))
        }

    }

    private fun import() {
        importIntent.launch("*/*")
    }

    private val exportIntent = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri: Uri? ->
        Timber.e("!!! uri export :$uri")
        // TODO load current content from database and stream it to a file
        // save a file with the given uri
        uri?.let {
            val arguments = Bundle().apply {
                putString(ExportingDialog.URI_KEY, uri.toString())
            }
            navigate(NavigateTo(R.id.dialogExport, args = arguments))
        }

    }


    private fun export() {
        exportIntent.launch("export.csv")
    }
}


