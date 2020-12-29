package de.ka.jamit.tcalc.ui.home.user

import android.os.Bundle
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.base.events.ShowSnack
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.User
import de.ka.jamit.tcalc.ui.home.addedit.HomeAddEditDialog
import de.ka.jamit.tcalc.ui.home.user.addedit.UserAddEditDialog
import de.ka.jamit.tcalc.utils.Snacker
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import de.ka.jamit.tcalc.utils.with
import io.reactivex.rxkotlin.addTo
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import timber.log.Timber

/**
 * A ViewModel for updating or creating a new home entry.
 *
 * Created by Thomas Hofmann on 03.07.20
 **/
class UserDialogViewModel
@ViewModelInject constructor(@Assisted private val stateHandle: SavedStateHandle,
                             val schedulerProvider: SchedulerProvider,
                             val resourcesProvider: ResourcesProvider,
                             val repository: Repository) : BaseViewModel() {

    val adapter = UserListAdapter(resourcesProvider = resourcesProvider)

    fun itemAnimator() = SlideInDownAnimator()

    private val itemListener: (UserListItemViewModel) -> Unit = {
        it.item?.let { user ->
            repository.selectUser(user.id)
        }

    }

    private val editListener: (UserListItemViewModel) -> Unit = {
        it.item?.let { user ->
            val arguments = Bundle().apply {
                putBoolean(UserAddEditDialog.UPDATE_KEY, true)
                putString(UserAddEditDialog.TITLE_KEY, user.name)
                putInt(HomeAddEditDialog.ID_KEY, user.id)
            }
            navigateTo(R.id.dialogUserAddEdit, args = arguments)
        }
    }

    private val deletionListener: () -> Unit = {
        handle(ShowDialogSnack(ShowSnack(
                message = resourcesProvider.getString(R.string.user_delete_undo_title),
                type = Snacker.SnackType.DEFAULT,
                actionText = resourcesProvider.getString(R.string.user_delete_undo_action),
                actionListener = { repository.undoDeleteLastUser() })))
    }

    private val addListener: () -> Unit = {
        navigateTo(R.id.dialogUserAddEdit)
    }

    init {
        repository.observeUsers()
                .with(schedulerProvider)
                .subscribe({ users ->
                    val items = users.map { user ->
                        UserListItemViewModel(
                                repository = repository,
                                item = user,
                                clickListener = itemListener,
                                editListener = editListener,
                                deletionListener = deletionListener)
                    }.toMutableList()
                    // add more item
                    items.add(UserListItemViewModel(
                            repository = repository,
                            item = User(-1),
                            moreClickListener = addListener))
                    adapter.setItems(items)
                }, { error ->
                    Timber.e(error, "While observing user data.")
                }).addTo(compositeDisposable)
    }

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun onClose() {
        handle(Close())
    }

    class Close
    class ShowDialogSnack(val snack: ShowSnack)
}