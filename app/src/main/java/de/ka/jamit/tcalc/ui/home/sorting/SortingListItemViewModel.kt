package de.ka.jamit.tcalc.ui.home.sorting

import android.view.View
import de.ka.jamit.tcalc.base.BaseItemViewModel
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider

/**
 * A list item ViewModel for sorting.
 *
 * Created by Thomas Hofmann on 12/29/20
 **/
class SortingListItemViewModel(val resourcesProvider: ResourcesProvider,
                               val item: HomeListAdapter.Sorting,
                               val selected: Boolean,
                               private val listener: (SortingListItemViewModel) -> Unit) : BaseItemViewModel() {

    val checkVisibility = if (selected) View.VISIBLE else View.INVISIBLE

    val title = resourcesProvider.getString(item.type.titleRes)

    fun onClick() {
        listener(this)
    }
}