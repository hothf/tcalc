package de.ka.jamit.arch.utils

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import de.ka.jamit.arch.R
import de.ka.jamit.arch.base.events.NavigateTo
import timber.log.Timber

object NavigationUtils {

    /**
     * Lists all possible animation types for navigation destinations.
     */
    enum class AnimType {
        DEFAULT, MODAL, NONE
    }

    /**
     * Indicate to simply go back and popup the last fragment.
     */
    const val BACK = -1

    /**
     * Indicate to popup to the given [NavigateTo.navigationTargetId]. Has to be supplied!
     */
    const val POPUPTO = -2

    /**
     * Navigates to the destination described in the [NavigateTo] event with the given [NavController].
     *
     * @param navController the nav controller to use for the navigation
     * @param navigateToEvent the event to use for directions and actions
     */
    fun navigateTo(navController: NavController, navigateToEvent: NavigateTo) {
        if (navigateToEvent.navigationTargetId == BACK) {
            navController.popBackStack()
            return
        } else if (navigateToEvent.navigationTargetId == POPUPTO) {
            navController.popBackStack(navigateToEvent.navigationPopupToId!!, true)
            return
        }

        val navOptions = setupOptions(
                navigateToEvent.clearBackStack,
                navController,
                navigateToEvent.navOptions,
                navigateToEvent.navigationPopupToId,
                navigateToEvent.animType
        )

        try {
            navController.navigate(
                    navigateToEvent.navigationTargetId,
                    navigateToEvent.args,
                    navOptions,
                    navigateToEvent.extras
            )
        } catch (e: Exception) {
            Timber.e(e, "Could somehow not navigate because of unknown nav destination.")
        }
    }

    private fun setupOptions(
            clearBackStack: Boolean,
            navController: NavController,
            navOptions: NavOptions?,
            popupToId: Int?,
            animType: AnimType
    ): NavOptions {
        return if (navOptions == null) {
            NavOptions.Builder().apply {
                setAnims(animType, this)

                if (clearBackStack) {
                    setPopUpTo(navController.graph.id, true)
                } else {
                    popupToId?.let { setPopUpTo(it, true) }
                }
            }.build()
        } else {
            NavOptions.Builder().apply {
                setAnims(animType, this)

                setLaunchSingleTop(navOptions.shouldLaunchSingleTop())

                if (clearBackStack) {
                    setPopUpTo(navController.graph.id, true)
                } else {
                    popupToId?.let { setPopUpTo(it, true) }
                }
            }.build()

        }
    }

    private fun setAnims(animType: AnimType, builder: NavOptions.Builder) {
        when (animType) {
            AnimType.DEFAULT -> builder.apply {
                setEnterAnim(R.anim.nav_default_enter_anim)
                setExitAnim(R.anim.nav_default_exit_anim)
                setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            }
            AnimType.MODAL -> builder.apply {
                setEnterAnim(R.anim.alternative_enter_anim)
                setExitAnim(R.anim.alternative_exit_anim)
                setPopEnterAnim(R.anim.alternative_pop_enter_anim)
                setPopExitAnim(R.anim.alternative_pop_exit_anim)
            }
            AnimType.NONE -> builder.apply {
                setEnterAnim(R.anim.fastfade_enter_anim)
                setExitAnim(R.anim.fastfade_exit_anim)
                setPopEnterAnim(R.anim.fastfade_pop_enter_anim)
                setPopExitAnim(R.anim.fastfade_pop_exit_anim)
            }
        }
    }
}