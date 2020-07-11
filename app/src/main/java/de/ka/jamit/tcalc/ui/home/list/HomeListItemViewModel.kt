package de.ka.jamit.tcalc.ui.home.list

import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseItemViewModel
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import org.koin.core.inject

/**
 * A ViewModel for a home list item.
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeListItemViewModel(val item: RecordDao,
                            private val listener: ((HomeListItemViewModel) -> Unit)? = null,
                            private val removeListener: (() -> Unit)? = null) :
        BaseItemViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()

    val isHeader = listener == null
    val title = AppDatabase.getTranslatedStringForKey(resourcesProvider, item.key)
    val alpha = if (item.isConsidered) 1.0f else 0.5f
    val value = item.value.toString()
    val timeSpan = resourcesProvider.getString(item.timeSpan.translationRes)
    val categoryImage = item.category.resId
    val valueTextColor = if (item.isIncome) resourcesProvider.getColor(R.color.fontColorPositive) else resourcesProvider.getColor(R.color.fontColorNegative)
    val valueImage = if (item.isIncome) R.drawable.ic_add else R.drawable.ic_remove
    val categoryShade = resourcesProvider.getColor(item.category.shadeRes)

    /**
     * Called on a click of the item.
     */
    fun onClick() {
        listener?.invoke(this)
    }

    /**
     * Called on a dismiss of the item (when being deleted for example).
     */
    fun onDismissed() {
        repository.deleteRecord(item.id)
        removeListener?.invoke()
    }
}