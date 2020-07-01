package de.ka.jamit.arch.utils

import io.reactivex.subjects.PublishSubject

/**
 * Listens for back press events, not triggered from the actual back button.
 */
class CloseEventListener {

    val observableClose: PublishSubject<Boolean> = PublishSubject.create()

    /**
     * Called on a wanted back press.
     */
    fun onClose() {
        observableClose.onNext(true)
    }
}