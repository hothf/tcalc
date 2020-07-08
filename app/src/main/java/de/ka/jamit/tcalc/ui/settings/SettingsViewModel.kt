package de.ka.jamit.tcalc.ui.settings

import de.ka.jamit.tcalc.base.BaseViewModel

class SettingsViewModel : BaseViewModel() {

    fun onImport(){
        handle(Import())
    }

    fun onExport(){
        handle(Export())
    }

    class Import
    class Export
}