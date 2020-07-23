package de.ka.jamit.tcalc.ui.main

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import de.ka.jamit.tcalc.base.BaseViewModel
import de.ka.jamit.tcalc.utils.GlobalMessageEventListener
import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import de.ka.jamit.tcalc.utils.with
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel
@ViewModelInject constructor(
        @Assisted private val stateHandle: SavedStateHandle,
        val schedulerProvider: SchedulerProvider,
        messageListener: GlobalMessageEventListener
): BaseViewModel() {

    init {
        messageListener.observableGlobalMessage
                .with(schedulerProvider)
                .subscribeBy(onNext = { showMessage(it) }, onError = {})
                .addTo(compositeDisposable)
    }
}
