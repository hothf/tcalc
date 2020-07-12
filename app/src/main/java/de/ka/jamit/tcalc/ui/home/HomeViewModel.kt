package de.ka.jamit.tcalc.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.base.events.ShowSnack
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.ui.home.addedit.HomeAddEditDialog
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
import de.ka.jamit.tcalc.ui.home.list.HomeListItemViewModel
import de.ka.jamit.tcalc.utils.Snacker
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable


import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import org.koin.core.inject
import timber.log.Timber


class HomeViewModel : BaseViewModel() {

    private val resourcesProvider: ResourcesProvider by inject()
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
    val adapter = HomeListAdapter()

    fun itemAnimator() = SlideInDownAnimator()

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
            putBoolean(HomeAddEditDialog.CONSIDERED_KEY, it.item.isConsidered)
            putBoolean(HomeAddEditDialog.INCOME_KEY, it.item.isIncome)
            putLong(HomeAddEditDialog.ID_KEY, it.item.id)
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
                                    HomeListItemViewModel(item = record,
                                            listener = itemListener,
                                            removeListener = removeListener)
                                }.toMutableList()
                                showEmptyView.postValue(items.isEmpty())
                                items.add(HomeListItemViewModel(
                                        RecordDao(id = -1, userId = -1),
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

    private fun calc(data: List<RecordDao>) {
        resultVisibility.postValue(View.GONE)
        loadingVisibility.postValue(View.VISIBLE)
        repository.calc(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: Repository.CalculationResult ->
                    // TODO get unit from repository or elsewhere!
                    Timber.e("wat::: ${result.monthlyIncome}")
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
