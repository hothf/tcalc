package de.ka.jamit.tcalc.ui.home.user

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.base.BaseItemViewModel
import de.ka.jamit.tcalc.repo.db.UserDao

/**
 * A ViewModel for a user list item.
 *
 * Created by Thomas Hofmann on 07.07.20
 **/
class UserListItemViewModel(val item: UserDao,
                            private val listener: ((UserListItemViewModel) -> Unit)? = null) :
        BaseItemViewModel() {


    val title = item.name

    val checkVisibility = if (item.selected) View.VISIBLE else View.GONE

    /**
     * Called on a click of the item.
     */
    fun onClick() {
        listener?.invoke(this)
    }
}