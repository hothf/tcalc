package de.ka.jamit.tcalc.base

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import de.ka.jamit.tcalc.base.events.*
import de.ka.jamit.tcalc.utils.*
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
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
abstract class BaseViewModel : ViewModel(), KoinComponent {

    val events = QueueLiveEvent<Event>()

    val compositeDisposable = CompositeDisposable()

    private fun queueEvent(event: Event) = events.queueValue(event)

    /**
     * Called when new arguments in a [bundle] have been received.
     */
    open fun onArgumentsReceived(bundle: Bundle) {
        // to be implemented by children
    }

    /**
     * Fires an event to navigate to the given destination.
     *
     * **Note:** Consider not to use the method in your production code, if you want to get rid of Android Apis inside
     * the ViewModel. You could also just fire an event which will handle the navigate to in the fragment/activity.
     * This is here for convenience and rapid development.
     *
     * @param navigationTargetId the target to navigate to. May reference a destination or action,
     * pass -1 as id to simply pop the back stack.
     * @param clearBackStack clears the backstack. Note that you can not use [navigationPopupToId] when this is set to true
     * @param args optional arguments to pass for the target fragment
     * @param navOptions optional navigator options for setting the default animations and behaviour
     * @param extras options for the transaction, like shared views fro transitions
     * @param animType a type of animation, defaults to system animations
     * @param navigationPopupToId id for target to popup to. This will only work, if [clearBackStack] is set to false
     */
    fun navigateTo(
        @IdRes navigationTargetId: Int,
        clearBackStack: Boolean = false,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        extras: Navigator.Extras? = null,
        animType: NavigationUtils.AnimType = NavigationUtils.AnimType.DEFAULT,
        @IdRes popupToId: Int? = null
    ) {
        queueEvent(
            NavigateTo(
                navigationTargetId = navigationTargetId,
                clearBackStack = clearBackStack,
                args = args,
                navOptions = navOptions,
                extras = extras,
                animType = animType,
                navigationPopupToId = popupToId
            )
        )
    }

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