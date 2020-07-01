package de.ka.jamit.tcalc.ui.home

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.api.BasePeople

import de.ka.jamit.tcalc.utils.AndroidSchedulerProvider
import de.ka.jamit.tcalc.utils.start
import de.ka.jamit.tcalc.utils.with
import retrofit2.Response
import timber.log.Timber

class HomeViewModel : BaseViewModel() {

    var buttonVisibility = MutableLiveData<Int>().apply { postValue(View.VISIBLE) }
    var loadingVisibility = MutableLiveData<Int>().apply { postValue(View.GONE) }

    fun clicked() {
        repository.getPeople()
            .with(AndroidSchedulerProvider())
            .subscribe { result: Response<BasePeople?> -> handleResult(result) }
            .start(compositeDisposable, ::showLoading)
    }

    private fun handleResult(result: Response<BasePeople?>) {
        buttonVisibility.postValue(View.VISIBLE)
        loadingVisibility.postValue(View.GONE)

        Timber.e("loaded! > data=${result.body()}")
    }

    private fun showLoading() {
        buttonVisibility.postValue(View.GONE)
        loadingVisibility.postValue(View.VISIBLE)
    }

    fun requestPermissions() {
        handle(PermissionRequest())
    }

    class PermissionRequest

}
