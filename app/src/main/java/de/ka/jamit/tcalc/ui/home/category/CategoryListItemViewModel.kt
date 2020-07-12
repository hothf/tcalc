package de.ka.jamit.tcalc.ui.home.category

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseItemViewModel
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import org.koin.core.inject

/**
 * A ViewModel for a category list item.
 * Created by Thomas Hofmann on 10.07.20
 **/
class CategoryListItemViewModel(val item: RecordDao.Category,
                                val isSelected: Boolean = false,
                                private val listener: ((CategoryListItemViewModel) -> Unit)? = null) :
        BaseItemViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()

    val categoryImage = item.resId
    val selectedVisibility = if (isSelected) View.VISIBLE else View.GONE
    val categoryShade = resourcesProvider.getColor(item.shadeRes)

    /**
     * Called on a click of the item.
     */
    fun onClick() {
        listener?.invoke(this)
    }
}