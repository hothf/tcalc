package de.ka.jamit.tcalc.ui.home.list

import de.ka.jamit.tcalc.base.BaseItemViewModel

/**
 * A ViewModel for a home list item.
 * Created by Thomas Hofmann on 03.07.20
 **/
class HomeListItemViewModel(val title: String,
                            private val listener: ((HomeListItemViewModel) -> Unit)? = null) :
        BaseItemViewModel() {

    val isHeader = listener == null

    fun onClick() {
        listener?.invoke(this)
    }
}