package de.ka.jamit.tcalc.ui.home

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.ui.home.list.HomeListAdapter
import de.ka.jamit.tcalc.ui.home.list.HomeListItemViewModel
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider


import io.reactivex.rxkotlin.addTo
import org.koin.core.inject
import timber.log.Timber


class HomeViewModel : BaseViewModel() {

    var buttonVisibility = MutableLiveData<Int>().apply { postValue(View.VISIBLE) }
    var loadingVisibility = MutableLiveData<Int>().apply { postValue(View.GONE) }

    private val resourcesProvider: ResourcesProvider by inject()

    val adapter = HomeListAdapter()

    init {
        repository.observeRecords().subscribe({ records ->
            Timber.d("||| record: $records")
            val items = records.map { record ->
                HomeListItemViewModel(record.id.toString())
            }
            adapter.setItems(items)
        }, { error ->
            Timber.e(error, "While observing repo data.")
        }).addTo(compositeDisposable)

    }

    fun layoutManager() = LinearLayoutManager(resourcesProvider.getApplicationContext())

    fun clicked() {
        repository.saveRecord(RecordDao(id = 0, key = "hello"))
    }


    private fun showLoading() {
        buttonVisibility.postValue(View.GONE)
        loadingVisibility.postValue(View.VISIBLE)
    }
}
