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


class SettingsFragment :
        BaseFragment<FragmentSettingsBinding, SettingsViewModel>(R.layout.fragment_settings, SettingsViewModel::class) {

    override fun onHandle(element: Any?) {
        super.onHandle(element)
        if (element is SettingsViewModel.Import) {
            import()
        } else if (element is SettingsViewModel.Export) {
            export(element.name)
        }
    }

    private fun import() {
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val arguments = Bundle().apply { putString(ImportingDialog.URI_KEY, uri.toString()) }
                navigate(NavigateTo(R.id.dialogImport, args = arguments))
            }
        }.launch("*/*")
    }

    private fun export(name: String) {
        registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri: Uri? ->
            uri?.let {
                val arguments = Bundle().apply { putString(ExportingDialog.URI_KEY, uri.toString()) }
                navigate(NavigateTo(R.id.dialogExport, args = arguments))
            }
        }.launch("$name.csv")
    }
}