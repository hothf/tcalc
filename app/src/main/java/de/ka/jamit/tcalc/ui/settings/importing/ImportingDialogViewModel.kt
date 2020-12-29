package de.ka.jamit.tcalc.ui.settings.importing

import android.net.Uri
import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.repo.Repository
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
class ImportingDialogViewModel
@ViewModelInject constructor(
        @Assisted private val stateHandle: SavedStateHandle,
        val repository: Repository,
        val csvUtils: CSVUtils,
        val schedulerProvider: SchedulerProvider,
        val resourcesProvider: ResourcesProvider
) : BaseViewModel() {

    val errorVisibility = MutableLiveData<Int>(View.GONE)
    val loadingVisibility = MutableLiveData<Int>(View.GONE)
    val successVisibility = MutableLiveData<Int>(View.GONE)
    val lastImportText = MutableLiveData<String>("")

    private var lastUri: Uri? = null

    fun import(uri: Uri?) {
        uri?.let {
            successVisibility.postValue(View.GONE)
            errorVisibility.postValue(View.GONE)
            loadingVisibility.postValue(View.VISIBLE)
            lastUri = it
            csvUtils.importCSV(it)
                    .with(schedulerProvider)
                    .subscribe({
                        val lastResult = repository.lastImportResult
                        if (lastResult != null) {
                            loadingVisibility.postValue(View.GONE)
                            successVisibility.postValue(View.VISIBLE)
                            lastImportText.postValue(resourcesProvider.getString(
                                    R.string.import_success_text, lastResult.name, lastResult.recordCount))
                        }
                        handle(Completed(lastResult))
                    }, { error ->
                        loadingVisibility.postValue(View.GONE)
                        errorVisibility.postValue(View.VISIBLE)
                        Timber.e(error, "While importing")
                    }).addTo(compositeDisposable)
        }
    }

    fun onRetry() {
        import(lastUri)
    }

    fun onCancel() {
        handle(Completed())
    }

    class Completed(val lastImportResult: Repository.ImportResult? = null)
}