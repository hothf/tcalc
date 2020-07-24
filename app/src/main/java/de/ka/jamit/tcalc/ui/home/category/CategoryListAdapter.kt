package de.ka.jamit.tcalc.ui.home.category


import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import de.ka.jamit.tcalc.base.BaseAdapter
import de.ka.jamit.tcalc.base.BaseViewHolder
import de.ka.jamit.tcalc.databinding.ItemCategoryDefaultBinding
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider

/**
 * A adapter for category list items.
 *
 * Created by Thomas Hofmann on 10.07.20
 **/
class CategoryListAdapter(list: ArrayList<CategoryListItemViewModel> = arrayListOf(), resourcesProvider: ResourcesProvider) :
        BaseAdapter<CategoryListItemViewModel>(resourcesProvider, list, CategoryListAdapterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return CategoryListViewHolder(ItemCategoryDefaultBinding.inflate(layoutInflater, parent, false))
    }

    class CategoryListAdapterDiffCallback : DiffUtil.ItemCallback<CategoryListItemViewModel>() {
        override fun areItemsTheSame(oldItem: CategoryListItemViewModel,
                                     newItem: CategoryListItemViewModel): Boolean {
            return oldItem.item.id == newItem.item.id
        }

        override fun areContentsTheSame(oldItem: CategoryListItemViewModel,
                                        newItem: CategoryListItemViewModel): Boolean {
            return oldItem.item == newItem.item && oldItem.isSelected && newItem.isSelected
        }
    }
}

class CategoryListViewHolder(binding: ViewDataBinding) : BaseViewHolder<ViewDataBinding>
(binding) {
    override var swipeableView: View? = null
    override var isSwipeable = false
    override var isDraggable = false
}
