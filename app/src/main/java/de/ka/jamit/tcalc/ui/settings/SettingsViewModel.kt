package de.ka.jamit.tcalc.ui.settings

import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import org.koin.core.inject

class SettingsViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()

    fun onImport() {
        handle(Import())
    }

    fun onExport() {
        var name = repository.getCurrentlySelectedUser().name
        if (name.isEmpty()) {
            name = resourcesProvider.getString(R.string.export_fallback_name)
        }
        handle(Export(name))
    }

    class Import
    class Export(val name: String)
}