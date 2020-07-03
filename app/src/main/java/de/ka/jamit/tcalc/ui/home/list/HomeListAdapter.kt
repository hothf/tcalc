package de.ka.jamit.tcalc.ui.home.list


import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import de.ka.jamit.tcalc.base.BaseAdapter
import de.ka.jamit.tcalc.base.BaseViewHolder
import de.ka.jamit.tcalc.databinding.ItemHomeDefaultBinding
import de.ka.jamit.tcalc.databinding.ItemHomeHeaderBinding

/**
 * A adapter for home list items.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeListAdapter(list: ArrayList<HomeListItemViewModel> = arrayListOf()) :
        BaseAdapter<HomeListItemViewModel>(list, HomeListAdapterDiffCallback()) {

    override fun onItemDismiss(position: Int) {
        getItems()[position].onDismissed()
        super.onItemDismiss(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItems()[position].isHeader) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return if (viewType == 1) HomeListHeaderViewHolder(ItemHomeHeaderBinding.inflate
        (layoutInflater, parent, false))
        else HomeListViewHolder(ItemHomeDefaultBinding.inflate(layoutInflater, parent, false))
    }

    class HomeListAdapterDiffCallback : DiffUtil.ItemCallback<HomeListItemViewModel>() {
        override fun areItemsTheSame(oldItem: HomeListItemViewModel,
                                     newItem: HomeListItemViewModel): Boolean {
            return oldItem.item.id == newItem.item.id
        }

        override fun areContentsTheSame(oldItem: HomeListItemViewModel,
                                        newItem: HomeListItemViewModel): Boolean {
            return oldItem.title == newItem.title && oldItem.value == newItem.value
        }
    }
}

class HomeListViewHolder(binding: ItemHomeDefaultBinding) : BaseViewHolder<ViewDataBinding>(binding) {
    override var swipeableView: View? = binding.swipeableContainer
    override var isSwipeable = true
    override var isDraggable = false
}

class HomeListHeaderViewHolder(binding: ViewDataBinding) : BaseViewHolder<ViewDataBinding>
(binding) {
    override var swipeableView: View? = null
    override var isSwipeable = false
    override var isDraggable = false
}
