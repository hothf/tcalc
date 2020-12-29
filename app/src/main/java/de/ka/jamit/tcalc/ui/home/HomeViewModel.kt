package de.ka.jamit.tcalc.ui.home

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
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
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
    val sortingText = MutableLiveData<String>(resourcesProvider.getString(adapter.currentSorting.type.titleRes))

    fun itemAnimator() = SlideInDownAnimator()

    fun onUserClicked() {
        handle(UserDialog())
    }

    fun onSortingClicked() {
        handle(SortClick(adapter.currentSorting))
    }

    fun sort(currentSorting: HomeListAdapter.Sorting) {
        adapter.currentSorting = currentSorting
        sortingText.postValue(resourcesProvider.getString(adapter.currentSorting.type.titleRes))
    }

    private val itemListener: (HomeListItemViewModel) -> Unit = {
       handle(ItemClick(it.item))
    }

    private val addListener = {
        handle(Add())
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

    class SortClick(val currentSorting: HomeListAdapter.Sorting)
    class UserDialog
    class Add
    class ItemClick(val item: Record)
}
