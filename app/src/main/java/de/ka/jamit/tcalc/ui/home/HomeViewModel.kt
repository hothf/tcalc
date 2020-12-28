package de.ka.jamit.tcalc.ui.home

import android.os.Bundle
import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.base.events.ShowSnack
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.Record
import de.ka.jamit.tcalc.repo.db.User
import de.ka.jamit.tcalc.ui.home.addedit.HomeAddEditDialog
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter.Companion.LOADING_ITEM_ID
import de.ka.jamit.tcalc.ui.home.list.HomeListItemViewModel
import de.ka.jamit.tcalc.utils.Snacker
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import de.ka.jamit.tcalc.utils.with
import io.reactivex.disposables.Disposable


import io.reactivex.rxkotlin.addTo
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import timber.log.Timber


class HomeViewModel
@ViewModelInject constructor(
        @Assisted private val stateHandle: SavedStateHandle,
        val repository: Repository,
        val schedulerProvider: SchedulerProvider,
        val resourcesProvider: ResourcesProvider
) : BaseViewModel() {

    private var users: Disposable? = null
    private var userRecords: Disposable? = null

    val emptyViewText = MutableLiveData<String>(resourcesProvider.getString(R.string.home_empty_text))
    val deltaTextColor = MutableLiveData<Int>(resourcesProvider.getColor(R.color.fontDefault))
    val emptyViewImageRes = MutableLiveData<Int>(R.drawable.ic_file)
    val loadingVisibility = MutableLiveData<Int>(View.GONE)
    val resultVisibility = MutableLiveData<Int>(View.GONE)
    val showEmptyView = MutableLiveData<Boolean>(false)
    val resultMonthlyIncomeText = MutableLiveData<String>("")
    val resultYearlyIncomeText = MutableLiveData<String>("")
    val resultMonthlyOutputText = MutableLiveData<String>("")
    val resultYearlyOutputText = MutableLiveData<String>("")
    val resultMonthlyDeltaText = MutableLiveData<String>("")
    val resultYearlyDeltaText = MutableLiveData<String>("")
    val userText = MutableLiveData<String>("")
    val adapter = HomeListAdapter(resourcesProvider = resourcesProvider)

    fun itemAnimator() = SlideInDownAnimator()

    fun onUserClicked() {
        navigateTo(R.id.dialogUser)
    }

    fun onSortingClicked(){
        adapter.toggleSort()
    }

    private val itemListener: (HomeListItemViewModel) -> Unit = {
        val arguments = Bundle().apply {
            putBoolean(HomeAddEditDialog.UPDATE_KEY, true)
            putString(HomeAddEditDialog.TITLE_KEY, it.item.key)
            putFloat(HomeAddEditDialog.VALUE_KEY, it.item.value)
            putInt(HomeAddEditDialog.TIMESPAN_KEY, it.item.timeSpan.id)
            putInt(HomeAddEditDialog.CATEGORY_KEY, it.item.category.id)
            putBoolean(HomeAddEditDialog.CONSIDERED_KEY, it.item.isConsidered)
            putBoolean(HomeAddEditDialog.INCOME_KEY, it.item.isIncome)
            putInt(HomeAddEditDialog.ID_KEY, it.item.id)
        }
        navigateTo(R.id.dialogHomeAdd, args = arguments)
    }

    private val addListener = {
        navigateTo(R.id.dialogHomeAdd)
    }

    private val removeListener = {
        showMessage(ShowSnack(
                message = resourcesProvider.getString(R.string.home_delete_undo_title),
                type = Snacker.SnackType.DEFAULT,
                actionText = resourcesProvider.getString(R.string.home_delete_undo_action),
                actionListener = { repository.undoDeleteLastRecord() }))
    }

    init {
        startObserving()
    }

    private fun startObserving() {
        users?.let(compositeDisposable::remove)
        users = repository.observeUsers()
                .with(schedulerProvider)
                .subscribe({ users: List<User> ->
                    val selected = users.firstOrNull { it.selected } ?: return@subscribe
                    userText.postValue(selected.name)
                    userRecords?.let(compositeDisposable::remove)
                    userRecords = repository.observeRecordsOfCurrentlySelected()
                            .with(schedulerProvider)
                            .subscribe({ records ->
                                val items = records.map { record ->
                                    HomeListItemViewModel(
                                            resourcesProvider = resourcesProvider,
                                            repository = repository,
                                            item = record,
                                            listener = itemListener,
                                            removeListener = removeListener)
                                }.toMutableList()
                                showEmptyView.postValue(items.isEmpty())
                                items.add(HomeListItemViewModel(
                                        resourcesProvider = resourcesProvider,
                                        repository = repository,
                                        item = Record(id = LOADING_ITEM_ID, userId = LOADING_ITEM_ID),
                                        moreListener = addListener))
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

    private fun calc(data: List<Record>) {
        resultVisibility.postValue(View.GONE)
        loadingVisibility.postValue(View.VISIBLE)
        repository.calc(data)
                .with(schedulerProvider)
                .subscribe({ result: Repository.CalculationResult ->
                    // TODO get unit from repository or elsewhere!
                    resultMonthlyIncomeText.postValue(
                            String.format(resourcesProvider.getString(R.string.home_result_format),
                                    result.monthlyIncome, "€"))
                    resultMonthlyOutputText.postValue(
                            String.format(resourcesProvider.getString(R.string.home_result_format),
                                    result.monthlyOutput, "€"))
                    resultMonthlyDeltaText.postValue(
                            String.format(resourcesProvider.getString(R.string.home_result_format),
                                    result.monthlyDifference, "€"))

                    resultYearlyIncomeText.postValue(
                            String.format(resourcesProvider.getString(R.string.home_result_format),
                                    result.yearlyIncome, "€"))
                    resultYearlyOutputText.postValue(
                            String.format(resourcesProvider.getString(R.string.home_result_format),
                                    result.yearlyOutput, "€"))
                    resultYearlyDeltaText.postValue(
                            String.format(resourcesProvider.getString(R.string.home_result_format),
                                    result.yearlyDifference, "€"))

                    if (result.monthlyDifference < 0) {
                        deltaTextColor.postValue(resourcesProvider.getColor(R.color.fontColorNegative))
                    } else {
                        deltaTextColor.postValue(resourcesProvider.getColor(R.color.fontColorPositive))
                    }

                    loadingVisibility.postValue(View.GONE)
                    resultVisibility.postValue(View.VISIBLE)
                }, { error ->
                    Timber.e(error, "While calculating")
                    loadingVisibility.postValue(View.GONE)
                    resultVisibility.postValue(View.VISIBLE)
                }).addTo(compositeDisposable)
    }
}
