package de.ka.jamit.tcalc.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.repo.db.UserDao
import de.ka.jamit.tcalc.ui.home.addedit.HomeAddEditDialog
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
import de.ka.jamit.tcalc.ui.home.list.HomeListItemViewModel
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable


import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import timber.log.Timber


class HomeViewModel : BaseViewModel() {

    var loadingVisibility = MutableLiveData<Int>().apply { postValue(View.GONE) }
    val resultText = MutableLiveData<String>("")
    val userPosition = MutableLiveData(-1)
    val adapter = HomeListAdapter()

    private var userRecords: Disposable? = null
    private var lastPosition: Int = 0

    private var currentUsers = emptyList<UserDao>()

    fun userAdapter(): SpinnerAdapter {
        val tempUsers = listOf("1dad", "2sdsd", "3ed")
        val users = tempUsers.toTypedArray()
        return ArrayAdapter<String>(
                resourcesProvider.getApplicationContext(),
                android.R.layout.simple_spinner_item,
                users).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private val userPositionObserver: (Int) -> Unit = { position ->
        if (currentUsers.isNotEmpty() && position != lastPosition) {
            repository.selectUser(currentUsers[position].id)

            userRecords?.let(compositeDisposable::remove)
            userRecords = repository.observeRecords()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ records ->
                        Timber.d("||| record: $records")
                        val items = records.map { record ->
                            HomeListItemViewModel(record, itemListener)
                        }
                        adapter.setItems(items)
                        calc(records)
                    }, { error ->
                        Timber.e(error, "While observing record data.")
                    }).addTo(compositeDisposable)
        }
        lastPosition = position
    }
    private val resourcesProvider: ResourcesProvider by inject()
    private val itemListener: (HomeListItemViewModel) -> Unit = {
        val arguments = Bundle().apply {
            putBoolean(HomeAddEditDialog.UPDATE_KEY, true)
            putString(HomeAddEditDialog.TITLE_KEY, it.item.key)
            putFloat(HomeAddEditDialog.VALUE_KEY, it.item.value)
            putInt(HomeAddEditDialog.TIMESPAN_KEY, it.item.timeSpan.id)
            putLong(HomeAddEditDialog.ID_KEY, it.item.id)
        }
        navigateTo(R.id.dialogHomeAdd, args = arguments)
    }

    init {
        userPosition.observeForever(userPositionObserver)

        repository.observeUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ users ->
                    val wasUsers = currentUsers
                    currentUsers = users
                    if (wasUsers.isEmpty()) { // if this is the first time, select it
                        val position = users.indexOfFirst { it.selected }
                        userPosition.postValue(position)
                    }
                }, { error ->
                    Timber.e(error, "While observing user data.")
                }).addTo(compositeDisposable)
    }

    private fun calc(data: List<RecordDao>) {
        loadingVisibility.postValue(View.VISIBLE)
        repository.calc(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ sum ->
                    resultText.postValue(sum.toString())
                    loadingVisibility.postValue(View.GONE)
                }, { error ->
                    Timber.e(error, "While calculating")
                    loadingVisibility.postValue(View.GONE)
                }
                ).addTo(compositeDisposable)
    }

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun onAddClicked() {
        navigateTo(R.id.dialogHomeAdd)
    }

    override fun onCleared() {
        userPosition.removeObserver(userPositionObserver)
        super.onCleared()
    }
}
