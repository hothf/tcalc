package de.ka.jamit.tcalc.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseActivity
import de.ka.jamit.tcalc.base.events.ShowSnack
import de.ka.jamit.tcalc.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main, MainViewModel::class) {

    override fun onSupportNavigateUp() = findNavController(this, R.id.main_nav_host_fragment).navigateUp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = findNavController(this, R.id.main_nav_host_fragment)

        navController.addOnDestinationChangedListener { _, dest: NavDestination, _ ->
            if (dest.id == R.id.detailFragment) {
                getBinding<ActivityMainBinding>()?.bottomNavigation?.visibility = View.GONE
            } else {
                getBinding<ActivityMainBinding>()?.bottomNavigation?.visibility = View.VISIBLE
            }
        }

        getBinding<ActivityMainBinding>()?.bottomNavigation?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    override fun onShowMessage(showSnack: ShowSnack) {
        getBinding<ActivityMainBinding>()?.mainSnacker?.reveal(showSnack)
    }

    override fun onFinish() {
        // we handle back presses in this example somewhere differently. This is why we should finish the app at some
        // point. This is triggered by listening to a close event and then forwarded to this point, where we simply
        // finish the app.
        finish()
    }
}
