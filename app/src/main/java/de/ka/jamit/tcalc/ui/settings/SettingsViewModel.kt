package de.ka.jamit.tcalc.ui.settings

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider

class SettingsViewModel
@ViewModelInject constructor(
        @Assisted private val stateHandle: SavedStateHandle,
        val repository: Repository,
        val resourcesProvider: ResourcesProvider
): BaseViewModel() {

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