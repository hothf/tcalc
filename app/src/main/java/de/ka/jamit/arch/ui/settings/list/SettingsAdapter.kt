package de.ka.jamit.arch.ui.settings.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import de.ka.jamit.arch.base.BaseAdapter
import de.ka.jamit.arch.base.BaseViewHolder
import de.ka.jamit.arch.databinding.ItemSettingsBinding

class SettingsAdapter(list: ArrayList<SettingsItemViewModel> = arrayListOf(),
                      clickListener: (SettingsItemViewModel) -> Unit) :
        BaseAdapter<SettingsItemViewModel>(list, SettingsAdapterDiffCallback()) {

    init {
        val newList = ArrayList<SettingsItemViewModel>()

        for (i in 1..6) {
            newList.add(SettingsItemViewModel(SettingsItem("$i", i), clickListener))
        }

        addItems(newList)
    }

    override fun onItemDismiss(position: Int) {
        val list = getItems().toMutableList().apply {
            removeAt(position)
        }
        setItems(list)
        super.onItemDismiss(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return SettingItemViewHolder(ItemSettingsBinding.inflate(layoutInflater, parent, false))
    }

    fun sort() {
        setItems(ArrayList(getItems().asReversed()))
    }

    class SettingsAdapterDiffCallback : DiffUtil.ItemCallback<SettingsItemViewModel>() {
        override fun areItemsTheSame(oldItem: SettingsItemViewModel,
                                     newItem: SettingsItemViewModel): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: SettingsItemViewModel,
                                        newItem: SettingsItemViewModel): Boolean {
            return oldItem.item == newItem.item
        }
    }
}

class SettingItemViewHolder<T : ItemSettingsBinding>(binding: T) : BaseViewHolder<T>(binding) {
    override var swipeableView: View? = binding.swipeableContainer
    override var isSwipeable = true
    override var isDraggable = false
}




