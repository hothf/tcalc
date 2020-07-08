package de.ka.jamit.tcalc.ui.settings

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseFragment
import de.ka.jamit.tcalc.databinding.FragmentSettingsBinding
import timber.log.Timber

class SettingsFragment :
        BaseFragment<FragmentSettingsBinding, SettingsViewModel>(R.layout.fragment_settings, SettingsViewModel::class) {

    override fun onHandle(element: Any?) {
        super.onHandle(element)
        if (element is SettingsViewModel.Import) {
            import()
        } else if (element is SettingsViewModel.Export){
            export()
        }
    }


    private val importIntent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Timber.e("!!! uri import :$uri")
        // TODO parse file from uri  and persist it in db if right format and anything
    }

    private fun import() {
        importIntent.launch("*/*")
    }

    private val exportIntent = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri: Uri? ->
        Timber.e("!!! uri export :$uri")
        // TODO load current content from database and stream it to a file
        // save a file with the given uri
    }


    private fun export(){
        exportIntent.launch("export.csv")
    }
}


