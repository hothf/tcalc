package de.ka.jamit.tcalc.ui.home.user


import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import de.ka.jamit.tcalc.base.BaseAdapter
import de.ka.jamit.tcalc.base.BaseViewHolder
import de.ka.jamit.tcalc.databinding.ItemHomeDefaultBinding
import de.ka.jamit.tcalc.databinding.ItemHomeHeaderBinding
import de.ka.jamit.tcalc.databinding.ItemUserDefaultBinding

/**
 * A adapter for user list items.
 *
 * Created by Thomas Hofmann on 07.07.20
 **/
class UserListAdapter(list: ArrayList<UserListItemViewModel> = arrayListOf()) :
        BaseAdapter<UserListItemViewModel>(list, UserListAdapterDiffCallback()) {

    override fun onItemDismiss(position: Int) {
//        getItems()[position].onDismissed()
        super.onItemDismiss(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return UserListViewHolder(ItemUserDefaultBinding.inflate(layoutInflater, parent, false))
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
