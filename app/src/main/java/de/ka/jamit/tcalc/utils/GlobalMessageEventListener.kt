package de.ka.jamit.tcalc.utils

import de.ka.jamit.tcalc.base.events.ShowSnack
import io.reactivex.subjects.PublishSubject

/**
 * Utility class for global messages.
 */
class GlobalMessageEventListener {

    val observableGlobalMessage: PublishSubject<ShowSnack> = PublishSubject.create()

    /**
     * Publishes a global message.
     *
     * @param snack the message to publish
     */
    fun publishMessage(snack: ShowSnack) {
        observableGlobalMessage.onNext(snack)
    }
}