package de.ka.jamit.tcalc.ui.home.category

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.GridLayoutManager
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.db.Category
import de.ka.jamit.tcalc.utils.DecorationUtil
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider

/**
 * A ViewModel for importing records.
 *
 * Created by Thomas Hofmann on 09.07.20
 **/
class CategoryDialogViewModel
@ViewModelInject constructor(@Assisted private val stateHandle: SavedStateHandle,
                             val resourcesProvider: ResourcesProvider) : BaseViewModel() {

    private var id: Int? = null

    fun layoutManager() = GridLayoutManager(resourcesProvider.getApplicationContext(), COLUMNS_COUNT)
    val adapter = CategoryListAdapter(resourcesProvider = resourcesProvider)
    val itemDecoration = DecorationUtil(
            resourcesProvider.getDimensionPixelSize(R.dimen.default_16),
            resourcesProvider.getDimensionPixelSize(R.dimen.default_16),
            COLUMNS_COUNT
    )

    fun updateWith(updatedId: Int){
        id = updatedId
        populateList()
    }

    private val listener: (CategoryListItemViewModel) -> Unit = {
        handle(Choose(id = it.item.id))
    }

    private fun populateList() {
        val items = Category.values().map {
            CategoryListItemViewModel(
                    resourcesProvider = resourcesProvider,
                    item = it,
                    isSelected = it.id == id,
                    listener = listener)
        }
        adapter.setItems(items)
    }

    fun onClose() {
        handle(Choose())
    }

    class Choose(val id: Int? = null)

    companion object {
        const val COLUMNS_COUNT = 3
    }
}