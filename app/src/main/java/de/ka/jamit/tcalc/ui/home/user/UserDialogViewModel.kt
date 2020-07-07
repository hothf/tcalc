package de.ka.jamit.tcalc.ui.home.user

import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.base.BaseViewModel
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

    init {
        repository.observeUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ users ->
                    val items = users.map { user ->
                        UserListItemViewModel(user, itemListener)
                    }
                    adapter.setItems(items)
                }, { error ->
                    Timber.e(error, "While observing user data.")
                }).addTo(compositeDisposable)
    }

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun choose() {
//        handle(Choose())
    }

    class Choose
}