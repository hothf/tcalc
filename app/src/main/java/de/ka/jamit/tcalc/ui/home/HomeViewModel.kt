package de.ka.jamit.tcalc.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.ui.home.dialog.HomeEnterDialog
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
import de.ka.jamit.tcalc.ui.home.list.HomeListItemViewModel
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider


import io.reactivex.rxkotlin.addTo
import org.koin.core.inject
import timber.log.Timber


class HomeViewModel : BaseViewModel() {

    var buttonVisibility = MutableLiveData<Int>().apply { postValue(View.VISIBLE) }
    var loadingVisibility = MutableLiveData<Int>().apply { postValue(View.GONE) }
    val adapter = HomeListAdapter()

    private val resourcesProvider: ResourcesProvider by inject()
    private val itemListener: (HomeListItemViewModel) -> Unit = {
        val arguments = Bundle().apply {
            putString(HomeEnterDialog.TITLE_KEY, it.item.key)
            putFloat(HomeEnterDialog.VALUE_KEY, it.item.value)
            putLong(HomeEnterDialog.ID_KEY, it.item.id)
        }
        navigateTo(R.id.dialogHomeEnter, args = arguments)
    }

    init {
        repository.observeRecords().subscribe({ records ->
            Timber.d("||| record: $records")
            val items = records.map { record ->
                HomeListItemViewModel(record, itemListener)
            }
            adapter.setItems(items)
        }, { error ->
            Timber.e(error, "While observing repo data.")
        }).addTo(compositeDisposable)

    }

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun onAddClicked() {
        repository.addRecord(key = "hello")
    }
}
