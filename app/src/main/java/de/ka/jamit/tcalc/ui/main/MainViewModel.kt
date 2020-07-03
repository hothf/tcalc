package de.ka.jamit.tcalc.ui.main

import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.utils.AndroidSchedulerProvider
import de.ka.jamit.tcalc.utils.with
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel : BaseViewModel() {

    init {
        messageListener.observableGlobalMessage
                .with(AndroidSchedulerProvider())
                .subscribeBy(onNext = { showMessage(it) }, onError = {})
                .addTo(compositeDisposable)
    }
}
