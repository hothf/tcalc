package de.ka.jamit.tcalc.ui.home.sorting

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import de.ka.jamit.tcalc.base.BaseAdapter
import de.ka.jamit.tcalc.base.BaseViewHolder
import de.ka.jamit.tcalc.databinding.ItemSortDefaultBinding
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider

/**
 * A sorting list adapter.
 *
 * Created by Thomas Hofmann on 29.12.20
 **/
class SortingListAdapter(
        listener: (SortingListItemViewModel) -> Unit,
        currentlySelectedItem: HomeListAdapter.Sorting? = null,
        resourcesProvider: ResourcesProvider,
        list: ArrayList<SortingListItemViewModel> =
                createSortingList(resourcesProvider, currentlySelectedItem, listener)) :
        BaseAdapter<SortingListItemViewModel>(resourcesProvider, list, SortingListAdapterDiffCallback()) {

    init {
        setItems(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return SortingListViewHolder(ItemSortDefaultBinding.inflate(layoutInflater, parent, false))
    }

    companion object {
        fun createSortingList(resourcesProvider: ResourcesProvider,
                              currentlySelectedItem: HomeListAdapter.Sorting? = null,
                              listener: (SortingListItemViewModel) -> Unit): ArrayList<SortingListItemViewModel> {
            return ArrayList<SortingListItemViewModel>().apply {
                HomeListAdapter.sorting.forEach {
                    add(SortingListItemViewModel(resourcesProvider, it, currentlySelectedItem == it, listener))
                }
            }
        }

        class SortingListAdapterDiffCallback : DiffUtil.ItemCallback<SortingListItemViewModel>() {
            override fun areItemsTheSame(oldItem: SortingListItemViewModel, newItem: SortingListItemViewModel): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: SortingListItemViewModel, newItem: SortingListItemViewModel): Boolean {
                return oldItem.item == newItem.item
            }
        }
    }

    class SortingListViewHolder(binding: ViewDataBinding) : BaseViewHolder<ViewDataBinding>(binding) {
        override var swipeableView: View? = null
        override var isSwipeable = false
        override var isDraggable = false
    }
}