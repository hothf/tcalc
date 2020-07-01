package de.ka.jamit.arch.ui.main

import de.ka.jamit.arch.base.BaseViewModel
import de.ka.jamit.arch.utils.AndroidSchedulerProvider
import de.ka.jamit.arch.utils.with
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel : BaseViewModel() {

    // here we listen to all sorts of global events, as the main ViewModel is connected with the MainActivity, which
    // is the only activity the app has, thus making all UIs on top of the fragment container globally visible at all
    // times and making the activity similar to a singleton.

    init {
        closeListener.observableClose
                .with(AndroidSchedulerProvider())
                .subscribeBy(onNext = { close() }, onError = {})
                .addTo(compositeDisposable)

        messageListener.observableGlobalMessage
                .with(AndroidSchedulerProvider())
                .subscribeBy(onNext = { showMessage(it) }, onError = {})
                .addTo(compositeDisposable)
    }
}
