package de.ka.jamit.tcalc.ui.home.user

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.ui.home.addedit.HomeAddEditDialog
import de.ka.jamit.tcalc.ui.home.user.addedit.UserAddEditDialog
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import timber.log.Timber

/**
 * A ViewModel for updating or creating a new home entry.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class UserDialogViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()

    val adapter = UserListAdapter()

    private val itemListener: (UserListItemViewModel) -> Unit = {
        repository.selectUser(it.item.id)
        handle(Choose())
    }

    private val editListener: (UserListItemViewModel) -> Unit = {
        val arguments = Bundle().apply {
            putBoolean(UserAddEditDialog.UPDATE_KEY, true)
            putString(UserAddEditDialog.TITLE_KEY, it.item.name)
            putBoolean(UserAddEditDialog.IS_SELECTED_KEY, it.item.selected)
            putLong(HomeAddEditDialog.ID_KEY, it.item.id)
        }
        handle(UserAddEdit(arguments))
    }

    private val deletionListener: () -> Unit = {
        handle(Choose())
    }

    init {
        repository.observeUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ users ->
                    val items = users.map { user ->
                        UserListItemViewModel(user, itemListener, editListener, deletionListener)
                    }
                    adapter.setItems(items)
                }, { error ->
                    Timber.e(error, "While observing user data.")
                }).addTo(compositeDisposable)
    }

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun choose() {
        val arguments = Bundle().apply {
            putBoolean(UserAddEditDialog.UPDATE_KEY, false)
        }
        handle(UserAddEdit(arguments))
    }

    class Choose
    class UserAddEdit(val args: Bundle? = null)
}