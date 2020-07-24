package de.ka.jamit.tcalc.ui.settings

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseFragment
import de.ka.jamit.tcalc.base.events.NavigateTo
import de.ka.jamit.tcalc.base.navigate
import de.ka.jamit.tcalc.databinding.FragmentHomeBinding
import de.ka.jamit.tcalc.databinding.FragmentSettingsBinding
import de.ka.jamit.tcalc.ui.home.HomeViewModel
import de.ka.jamit.tcalc.ui.settings.exporting.ExportingDialog
import de.ka.jamit.tcalc.ui.settings.importing.ImportingDialog

// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseSettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>(
        R.layout.fragment_settings,
        SettingsViewModel::class
)

@AndroidEntryPoint
class SettingsFragment : BaseSettingsFragment(){

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