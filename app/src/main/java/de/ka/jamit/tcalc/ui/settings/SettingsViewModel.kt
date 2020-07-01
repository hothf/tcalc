package de.ka.jamit.tcalc.ui.settings

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.ui.settings.list.SettingsAdapter
import de.ka.jamit.tcalc.ui.settings.list.SettingsItemViewModel
import de.ka.jamit.tcalc.utils.NavigationUtils
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import org.koin.core.inject

class SettingsViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()
    private val clickListener: (SettingsItemViewModel) -> Unit = {
        navigateTo(
            navigationTargetId = R.id.detailFragment,
            args = Bundle().apply { putString("Argument", it.title) },
            animType = NavigationUtils.AnimType.MODAL
        )
    }

    // Please note: These are just example implementations on how you can do it, you do not have to set your adapters
    // like this, if you prefer holding the correct data state differently. For me, it is a convenient way of having
    // everything related to later UI updates in mutable LiveData. The idea behind this is, that the adapter itself
    // is lifecycle aware with this approach.
    val adapter = SettingsAdapter(clickListener = clickListener)

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun sort() {
        adapter.sort()
    }
}