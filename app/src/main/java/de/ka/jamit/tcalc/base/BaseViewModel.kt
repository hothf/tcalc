package de.ka.jamit.tcalc.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import de.ka.jamit.tcalc.base.events.*
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * The *base view model*. Subclasses of this ViewModel should be used to separate sub classes of [BaseActivity]s or
 * [BaseFragment]s view representation, inflated via Databinding, and the business Logic of the app. ViewModels do
 * not directly know the activities or fragment layouts they are responsible for updating their state.
 *
 * The ViewModels will mainly offer mappings of data to `MutableLiveData` or such which will update views via
 * Databinding. Any access to user interactions, databases and/or repository is coordinated through the ViewModel. This
 * makes it easier to test pure business logic, as there will be only such in the ViewModels which themselves do not
 * depend on or use Android Apis directly  when possible, making the unit tests much easier and lightweight.
 *
 * Some methods of the ViewModel will trigger or generate events which will be evaluated by the Fragment or Activity
 * attached to it. This builds the bridge for callbacks from the ViewModel to the View for Android Api events the
 * ViewModel should not handle itself, like Permission Handling and so on. In this way, the ViewModel does not know
 * the View directly and can be exchanged at any time.
 *
 * The Activity or Fragment themselves apply the ViewModels and thus can call methods of the ViewModel **directly**.
 */
abstract class BaseViewModel : ViewModel() {

    val events = QueueLiveEvent<Event>()

    val compositeDisposable = CompositeDisposable()

    private fun queueEvent(event: Event) = events.queueValue(event)

    /**
     * Fires an event to show a message with [snack] options.
     */
    fun showMessage(snack: ShowSnack) {
        queueEvent(snack)
    }

    /**
     * Fires an event to let others know that a back press has been registered.
     */
    fun consumeBackPress() {
        queueEvent(ConsumeBackPress())
    }

    /**
     * Fires an event to let others know that a close the screen event has been registered
     */
    fun close() {
        queueEvent(Close())
    }

    /**
     * Fires an event to let others know that a screen should be opened. If a [url] is provided, will inform about a
     * wanted webview, otherwise a [clazz] informs about wanting to open an activity with optional [args] as arguments.
     */
    fun open(url: String? = null, clazz: KClass<*>? = null, args: Bundle? = null) {
        queueEvent(Open(url = url, clazz = clazz, args = args))
    }

    /**
     * A generic event handler. Pass a [element] to let either the Fragment or Activity in which this ViewModel has a
     * bound databinding going on handle the event.
     */
    fun <T> handle(element: T) {
        queueEvent(Handle(element = element))
    }

    override fun onCleared() {
        super.onCleared()

        Timber.i("Clearing viewModel: $this")

        compositeDisposable.clear()
    }
}