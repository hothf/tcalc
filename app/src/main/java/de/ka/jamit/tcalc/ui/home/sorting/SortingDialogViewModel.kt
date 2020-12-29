package de.ka.jamit.tcalc.ui.home.sorting

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator

/**
 * A sorting dialog ViewModel.
 *
 * Created by Thomas Hofmann on 29.12.20
 **/
class SortingDialogViewModel
@ViewModelInject constructor(
        @Assisted private val stateHandle: SavedStateHandle,
        val resourcesProvider: ResourcesProvider
) : BaseViewModel() {
    private val itemListener: (SortingListItemViewModel) -> Unit = {
        handle(Choose(it.item))
    }

    var adapter: MutableLiveData<SortingListAdapter?> = MutableLiveData(null)

    fun select(selectedItem: HomeListAdapter.Sorting){
        adapter.postValue(SortingListAdapter(
                resourcesProvider = resourcesProvider,
                currentlySelectedItem = selectedItem,
                listener = itemListener
        ))
    }

    fun itemAnimator() = SlideInDownAnimator()

    fun onClose() {
        handle(Close())
    }

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    class Close
    class Choose(val sorting: HomeListAdapter.Sorting)
}