package de.ka.jamit.tcalc.ui.settings.exporting

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.utils.CSVUtils
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import de.ka.jamit.tcalc.utils.with
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

/**
 * A ViewModel for importing records.
 *
 * Created by Thomas Hofmann on 09.07.20
 **/
class ExportingDialogViewModel
@ViewModelInject constructor(
        @Assisted private val stateHandle: SavedStateHandle,
        val csvUtils: CSVUtils,
        val schedulerProvider: SchedulerProvider,
        val resourcesProvider: ResourcesProvider
) : BaseViewModel() {

    val errorVisibility = MutableLiveData<Int>(View.GONE)
    val loadingVisibility = MutableLiveData<Int>(View.GONE)

    private var lastUri: Uri? = null

    fun export(uri: Uri?) {
        uri?.let {
            lastUri = it
            errorVisibility.postValue(View.GONE)
            loadingVisibility.postValue(View.VISIBLE)

            csvUtils.exportCSV(it)
                    .with(schedulerProvider)
                    .subscribe({
                        handle(Completed(it))
                    }, { error ->
                        loadingVisibility.postValue(View.GONE)
                        errorVisibility.postValue(View.VISIBLE)
                        Timber.e(error, "While exporting")
                    }).addTo(compositeDisposable)
        }
    }

    fun onRetry() {
        lastUri?.let(::export)
    }

    fun onCancel() {
        handle(Completed())
    }

    class Completed(val uri: Uri? = null)
}