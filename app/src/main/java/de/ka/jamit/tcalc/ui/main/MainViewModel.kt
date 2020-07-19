package de.ka.jamit.tcalc.ui.main

import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import de.ka.jamit.tcalc.utils.with
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.koin.core.inject

class MainViewModel : BaseViewModel() {

    private val schedulerProvider: SchedulerProvider by inject()

    init {
        messageListener.observableGlobalMessage
                .with(schedulerProvider)
                .subscribeBy(onNext = { showMessage(it) }, onError = {})
                .addTo(compositeDisposable)
    }
}
