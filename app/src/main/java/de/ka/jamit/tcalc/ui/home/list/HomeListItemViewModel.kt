package de.ka.jamit.tcalc.ui.home.list

import de.ka.jamit.tcalc.base.BaseItemViewModel
import de.ka.jamit.tcalc.repo.db.RecordDao

/**
 * A ViewModel for a home list item.
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeListItemViewModel(val item: RecordDao,
                            private val listener: ((HomeListItemViewModel) -> Unit)? = null) :
        BaseItemViewModel() {

    val isHeader = listener == null
    val title = item.id.toString()
    val value = item.value.toString()

    fun onClick() {
        listener?.invoke(this)
    }
}