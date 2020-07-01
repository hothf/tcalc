package de.ka.jamit.arch.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.CallSuper
import androidx.databinding.BaseObservable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import de.ka.jamit.arch.BR
import de.ka.jamit.arch.repo.Repository
import de.ka.jamit.arch.utils.resources.ResourcesProvider
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*
import kotlin.collections.ArrayList

/**
 * This is a implementation of a very basic base adapter.
 *
 * It makes building simple example lists very fast, although it is not
 * a very throughout built adapter.
 *
 * Feel free to use better libraries for this!
 *
 * **Important:** Set the right lifecycle owner of this adapter to make use of `MutableLiveData used in item `ViewModels`.
 */
abstract class BaseAdapter<E : BaseItemViewModel>(
        private val items: ArrayList<E> = arrayListOf(),
        private val diffCallback: DiffUtil.ItemCallback<E>? = null
) : RecyclerView.Adapter<BaseViewHolder<*>>(), KoinComponent {

    private var differ: AsyncListDiffer<E>? = null
    private val resourcesProvider: ResourcesProvider by inject()

    val layoutInflater: LayoutInflater = LayoutInflater.from(resourcesProvider.getApplicationContext())
    var isEmpty: Boolean = items.isEmpty()

    init {
        if (diffCallback != null) {
            @Suppress("LeakingThis")
            differ = AsyncListDiffer(this, diffCallback)
        }
    }

    @CallSuper
    open fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (diffCallback != null) {
            val list = differ!!.currentList.toMutableList()
            Collections.swap(list, fromPosition, toPosition)
            setItems(list)
        } else {
            Collections.swap(items, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
        }
        return true
    }

    @CallSuper
    open fun onItemDismiss(position: Int) {
        if (diffCallback != null) {
            // let subclasses handle it
        } else {
            items.removeAt(position)
            notifyItemRemoved(position)
            isEmpty = items.isEmpty()
        }
    }

    fun getItems(): List<E> {
        return if (diffCallback != null) {
            differ!!.currentList
        } else {
            items
        }
    }

    open fun clear() {
        if (diffCallback != null) {
            differ?.submitList(listOf())
        } else {
            items.clear()
            notifyDataSetChanged()
        }
    }

    open fun addItem(index: Int = 0, item: E) {
        if (diffCallback != null) {
            differ?.submitList(differ!!.currentList.toMutableList().apply { add(index, item) })
        } else {
            items.add(item)

            notifyDataSetChanged()
        }
    }

    open fun setItems(newItems: List<E>) {
        if (diffCallback != null) {
            differ?.submitList(newItems)
        } else {
            items.clear()

            items.addAll(newItems)

            notifyDataSetChanged()
        }
    }

    open fun addItems(newItems: List<E>) {
        if (diffCallback != null) {
            val list = differ?.currentList?.toMutableList()
            list?.addAll(newItems)
            differ?.submitList(list)
        } else {
            items.addAll(newItems)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        if (diffCallback != null) {
            return differ!!.currentList.size
        }
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (diffCallback != null) {
            holder.bind(differ!!.currentList[holder.adapterPosition])
        } else {
            holder.bind(items[holder.adapterPosition])
        }

        if (holder.adapterPosition in 0 until itemCount) {
            if (diffCallback != null) {
                differ!!.currentList[holder.adapterPosition].onAttached()
            } else {
                items[holder.adapterPosition].onAttached()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (diffCallback != null) {
            differ!!.currentList[position].type
        } else {
            items[position].type
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        if (holder.adapterPosition in 0 until itemCount) {
            if (diffCallback != null) {
                differ!!.currentList[holder.adapterPosition].onCleared()
            } else {
                items[holder.adapterPosition].onCleared()
            }
        }
        super.onViewRecycled(holder)
    }
}

/**
 * These viewModels are not created through the android ViewModel framework but still may be used
 * with `ObservableFields`.
 */
abstract class BaseItemViewModel(val type: Int = 0) : BaseObservable(), KoinComponent {

    val repository: Repository by inject()

    var compositeDisposable: CompositeDisposable? = null

    fun onAttached() {
        compositeDisposable = CompositeDisposable()
    }

    fun onCleared() {
        compositeDisposable?.clear()
    }
}

/**
 * A base view holder is capable of setting flags for letting a item touch helper know, if the holder should be able
 * to be swiped and/or dragged. If [isDraggable] is set to true, make sure to also set a valid [swipeableView] of the
 * item.
 */
abstract class BaseViewHolder<T : ViewDataBinding>(private val binding: T) :
        RecyclerView.ViewHolder(binding.root) {

    abstract var isDraggable: Boolean
    abstract var isSwipeable: Boolean
    abstract var swipeableView: View?

    fun bind(viewModel: BaseItemViewModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }

    open fun onHolderDrag() {
        // do implement in sub class
    }

    @CallSuper
    open fun onHolderSwipe(
            dX: Float,
            dY: Float,
            actionState: Int
    ) {
        swipeableView?.let {
            it.translationX = dX
        }
    }

    @CallSuper
    open fun onHolderClear() {
        swipeableView?.let {
            it.translationX = 0.0f
        }
    }
}