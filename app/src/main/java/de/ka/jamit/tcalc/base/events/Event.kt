package de.ka.jamit.tcalc.base.events

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import de.ka.jamit.tcalc.utils.NavigationUtils
import de.ka.jamit.tcalc.utils.Snacker
import kotlin.reflect.KClass

/**
 * A collection of commonly used events.
 */
sealed class Event

/**
 * An event for handling navigation to destinations.
 *
 * @param navigationTargetId the target to navigate to. May reference a destination or action
 * @param clearBackStack clears the backstack. Note that you can not use [navigationPopupToId] when this is set to true
 * @param args optional arguments to pass for the target fragment
 * @param navOptions optional navigator options for setting the default animations and behaviour
 * @param extras options for the transaction, like shared views fro transitions
 * @param animType a type of animation, defaults to system animations
 * @param navigationPopupToId id for target to popup to. This will only work, if [clearBackStack] is set to false
 */
data class NavigateTo(
    @IdRes val navigationTargetId: Int,
    val clearBackStack: Boolean = false,
    val args: Bundle? = null,
    val navOptions: NavOptions? = null,
    val extras: Navigator.Extras? = null,
    val animType: NavigationUtils.AnimType = NavigationUtils.AnimType.DEFAULT,
    @IdRes val navigationPopupToId: Int? = null
) : Event()

/**
 * An event for opening a screen or another activity.
 *
 * @param url a url to open up in a new browser window
 * @param clazz an activity class to open up
 * @param args optional arguments for the new screen
 */
data class Open(
    val url: String? = null,
    val clazz: KClass<*>? = null,
    val args: Bundle? = null
) : Event()

/**
 * An event to indicate that a back press has been registered.
 *
 * The optional [consumed] flag can indicate whether this event should be evaluated or not.
 */
data class ConsumeBackPress(val consumed: Boolean = true) : Event()

/**
 * An event to indicate that a close request of the current screen has been registered.
 *
 * The optional [consumed] flag can indicate whether this event should be evaluated or not.
 */
data class Close(val consumed: Boolean = true) : Event()

/**
 * An event for showing a message, generally a snack bar of some sorts.
 *
 * @param message the message to show
 * @param type the message type. Different types have different styles. Defaults to [Snacker.SnackType.DEFAULT]
 * @param actionText optional text for an action which may be shown next to the message
 * @param actionListener optional listener for the action to perform on a click of the [actionText].
 * May only be useful if such a text is given.
 */
data class ShowSnack(
    val message: String,
    val type: Snacker.SnackType = Snacker.SnackType.DEFAULT,
    val actionText: String? = null,
    val actionListener: (() -> Unit)? = null
) : Event()

/**
 * Serves as a generic event handler.
 *
 * Simply pass a [element] to be sent through the event system
 */
data class Handle<T>(
    val element: T
) : Event()


