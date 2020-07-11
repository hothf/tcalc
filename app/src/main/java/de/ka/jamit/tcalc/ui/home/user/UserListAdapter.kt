package de.ka.jamit.tcalc.ui.home.user


import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import de.ka.jamit.tcalc.base.BaseAdapter
import de.ka.jamit.tcalc.base.BaseViewHolder
import de.ka.jamit.tcalc.databinding.ItemUserAddBinding
import de.ka.jamit.tcalc.databinding.ItemUserDefaultBinding

/**
 * A adapter for user list items.
 *
 * Created by Thomas Hofmann on 07.07.20
 **/
class UserListAdapter(list: ArrayList<UserListItemViewModel> = arrayListOf()) :
        BaseAdapter<UserListItemViewModel>(list, UserListAdapterDiffCallback()) {

    override fun onItemDismiss(position: Int) {
        getItems()[position].let {
            if (!it.isDefaultItem || !it.isMoreItem) {
                it.onDismissed()
            }
        }
        super.onItemDismiss(position)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItems()[position]
        return if (item.isDefaultItem) 1 else if (item.isMoreItem) 2 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            2 -> UserMoreListViewHolder(ItemUserAddBinding.inflate(layoutInflater, parent, false))
            1 -> UserDefaultListViewHolder(ItemUserDefaultBinding.inflate(layoutInflater, parent, false))
            else -> UserListViewHolder(ItemUserDefaultBinding.inflate(layoutInflater, parent, false))
        }
    }

    class UserListAdapterDiffCallback : DiffUtil.ItemCallback<UserListItemViewModel>() {
        override fun areItemsTheSame(oldItem: UserListItemViewModel,
                                     newItem: UserListItemViewModel): Boolean {
            return oldItem.item.id == newItem.item.id
        }

        override fun areContentsTheSame(oldItem: UserListItemViewModel,
                                        newItem: UserListItemViewModel): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.item == newItem.item
        }
    }
}

class UserListViewHolder(binding: ItemUserDefaultBinding) : BaseViewHolder<ViewDataBinding>(binding) {
    override var swipeableView: View? = binding.swipeableContainer
    override var isSwipeable = true
    override var isDraggable = false
}

class UserDefaultListViewHolder(binding: ItemUserDefaultBinding) : BaseViewHolder<ViewDataBinding>(binding) {
    override var swipeableView: View? = null
    override var isSwipeable = true
    override var isDraggable = false
}

class UserMoreListViewHolder(binding: ItemUserAddBinding) : BaseViewHolder<ViewDataBinding>(binding) {
    override var swipeableView: View? = null
    override var isSwipeable = true
    override var isDraggable = false
}