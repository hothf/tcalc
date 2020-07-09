package de.ka.jamit.tcalc.ui.settings.exporting

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.utils.CSVUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.koin.core.inject
import timber.log.Timber

/**
 * A ViewModel for importing records.
 *
 * Created by Thomas Hofmann on 09.07.20
 **/
class ExportingDialogViewModel : BaseViewModel() {

    private val csvUtils: CSVUtils by inject()

    val errorVisibility = MutableLiveData<Int>(View.GONE)
    val loadingVisibility = MutableLiveData<Int>(View.GONE)

    private var uri: Uri? = null

    override fun onArgumentsReceived(bundle: Bundle) {
        super.onArgumentsReceived(bundle)

        uri = bundle.getString(ExportingDialog.URI_KEY)?.toUri()
        export()
    }

    private fun export() {
        errorVisibility.postValue(View.GONE)
        loadingVisibility.postValue(View.VISIBLE)
        uri?.let {
            csvUtils.exportCSV(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
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
        export()
    }

    fun onCancel() {
        handle(Completed())
    }

    class Completed(val uri: Uri? = null)
}