package de.ka.jamit.tcalc.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.events.NavigateTo
import de.ka.jamit.tcalc.base.events.Open
import de.ka.jamit.tcalc.utils.NavigationUtils
import timber.log.Timber

/**
 * Defines all needed methods for creating a base for view binding and communication between fragment and viewModels
 * or activities and viewModels.
 *
 * Created by Thomas Hofmann on 22.04.20
 **/
interface BaseComposable {

    /**
     * Retrieves the view binding. May only be useful and return results after view creation.
     */
    fun <T> getBinding(): T?

    /**
     * Called when the composition of binding has been done. Useful for additional view state manipulations.
     */
    fun <T> onComposed(binding: T, savedInstanceState: Bundle?)

    /**
     * Called when a generic element should be handled.
     */
    fun onHandle(element: Any?)
}

/**
 * Opens a window with the specified info of [open] or tries to open a activity intent.
 */
fun Fragment.openUp(open: Open) {
    if (open.clazz != null) {

        val intent = Intent(activity, open.clazz.java)

        open.args?.let {
            intent.putExtras(it)
        }

        startActivity(intent)
    } else {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(open.url)))
    }
}

/**
 * Retrieves the base Activity, if it is of kind [BaseActivity], else returns null.
 */
fun Fragment.getBaseActivity(): BaseActivity<*, *>? {
    return (requireActivity() as? BaseActivity<*, *>)
}

/**
 * Navigates to the specified destination defined in [navigateTo].
 */
fun Fragment.navigate(navigateTo: NavigateTo) {
    val navController = try {
        view?.findNavController()
    } catch (exception: Exception) {
        Timber.e(exception, "While trying to find nav controller, using parent fragment manager instead")
        val host: Fragment? = parentFragmentManager.findFragmentById(R.id.main_nav_host_fragment)
        host?.let(NavHostFragment::findNavController)
    }

    if (navController == null) {
        Timber.e("Could not find nav controller!")
    } else {
        NavigationUtils.navigateTo(navController, navigateTo)
    }
}
