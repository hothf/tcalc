package de.ka.jamit.tcalc.ui.home.list


import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import de.ka.jamit.tcalc.base.BaseAdapter
import de.ka.jamit.tcalc.base.BaseViewHolder
import de.ka.jamit.tcalc.databinding.ItemHomeDefaultBinding
import de.ka.jamit.tcalc.databinding.ItemHomeHeaderBinding
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.min

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
            return oldItem.title == newItem.title
                    && oldItem.value == newItem.value
                    && oldItem.item == newItem.item
        }
    }
}

class HomeListViewHolder(val binding: ItemHomeDefaultBinding) : BaseViewHolder<ViewDataBinding>
(binding) {
    override var swipeableView: View? = binding.swipeableContainer
    override var isSwipeable = true
    override var isDraggable = false

    override fun onHolderClear() {
        binding.deleteLeftImage.alpha = 0.0f
        binding.deleteLeftImage.scaleX = 0.0f
        binding.deleteLeftImage.scaleY = 0.0f

        binding.deleteRightImage.alpha = 0.0f
        binding.deleteRightImage.scaleX = 0.0f
        binding.deleteRightImage.scaleY = 0.0f
        super.onHolderClear()
    }

    override fun onHolderSwipe(dX: Float, dY: Float, actionState: Int) {
        swipeableView?.let {
            val change = (abs(dX) / it.width) * 2
            if (dX < 0) {
                binding.deleteLeftImage.alpha = 0.0f + change
                binding.deleteLeftImage.scaleX = min(0.0f + change, 1.0f)
                binding.deleteLeftImage.scaleY = min(0.0f + change, 1.0f)
            } else {
                binding.deleteRightImage.alpha = 0.0f + change
                binding.deleteRightImage.scaleX = min(0.0f + change, 1.0f)
                binding.deleteRightImage.scaleY = min(0.0f + change, 1.0f)
            }
        }

        super.onHolderSwipe(dX, dY, actionState)
    }
}

class HomeListHeaderViewHolder(binding: ViewDataBinding) : BaseViewHolder<ViewDataBinding>
(binding) {
    override var swipeableView: View? = null
    override var isSwipeable = false
    override var isDraggable = false
}
