package de.ka.jamit.tcalc.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.RecordDao
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

    private val resourcesProvider: ResourcesProvider by inject()
    private var users: Disposable? = null
    private var userRecords: Disposable? = null

    var emptyViewText = MutableLiveData<String>(resourcesProvider.getString(R.string.home_empty_text))
    var emptyViewImageRes = MutableLiveData<Int>(R.drawable.ic_home)
    var loadingVisibility = MutableLiveData<Int>(View.GONE)
    var showEmptyView = MutableLiveData<Boolean>(false)
    val resultText = MutableLiveData<String>("")
    val userText = MutableLiveData<String>("")
    val adapter = HomeListAdapter()

    fun onUserClicked() {
        navigateTo(R.id.dialogUser)
    }

    private val itemListener: (HomeListItemViewModel) -> Unit = {
        val arguments = Bundle().apply {
            putBoolean(HomeAddEditDialog.UPDATE_KEY, true)
            putString(HomeAddEditDialog.TITLE_KEY, it.item.key)
            putFloat(HomeAddEditDialog.VALUE_KEY, it.item.value)
            putInt(HomeAddEditDialog.TIMESPAN_KEY, it.item.timeSpan.id)
            putInt(HomeAddEditDialog.CATEGORY_KEY, it.item.category.id)
            putLong(HomeAddEditDialog.ID_KEY, it.item.id)
        }
        navigateTo(R.id.dialogHomeAdd, args = arguments)
    }

    init {
        startObserving()
    }

    private fun startObserving() {
        users?.let(compositeDisposable::remove)
        users = repository.observeUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ users ->
                    Timber.e(users.toString())
                    val selected = users.firstOrNull { it.selected } ?: return@subscribe
                    userText.postValue(selected.name)
                    userRecords?.let(compositeDisposable::remove)
                    userRecords = repository.observeRecords()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ records ->
                                Timber.d("||| record: $records")
                                val items = records.map { record ->
                                    HomeListItemViewModel(record, itemListener)
                                }
                                showEmptyView.postValue(items.isEmpty())
                                adapter.setItems(items)
                                calc(records)
                            }, { error ->
                                Timber.e(error, "While observing record data.")
                            }).addTo(compositeDisposable)

                }, { error ->
                    Timber.e(error, "While observing user data.")
                }).addTo(compositeDisposable)
    }

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun onAddClicked() {
        navigateTo(R.id.dialogHomeAdd)
    }

    private fun calc(data: List<RecordDao>) {
        loadingVisibility.postValue(View.VISIBLE)
        repository.calc(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: Repository.CalculationResult ->
                    resultText.postValue("monthly: ${result.monthlyValue}, yearly: ${result.yearlyValue}")
                    loadingVisibility.postValue(View.GONE)
                }, { error ->
                    Timber.e(error, "While calculating")
                    loadingVisibility.postValue(View.GONE)
                }).addTo(compositeDisposable)
    }
}
