package de.ka.jamit.tcalc.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.base.BaseActivity
import de.ka.jamit.tcalc.base.events.ShowSnack
import de.ka.jamit.tcalc.databinding.ActivityMainBinding

// Workaround for https://github.com/google/dagger/issues/1904
abstract class BaseMainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
        R.layout.activity_main,
        MainViewModel::class
)

@AndroidEntryPoint
class MainActivity : BaseMainActivity() {

    override fun onSupportNavigateUp() = findNavController(this, R.id.main_nav_host_fragment).navigateUp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = findNavController(this, R.id.main_nav_host_fragment)

        navController.addOnDestinationChangedListener { _, _: NavDestination, _ ->
            getBinding<ActivityMainBinding>()?.bottomNavigation?.visibility = View.VISIBLE
        }

        getBinding<ActivityMainBinding>()?.bottomNavigation?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    override fun onShowMessage(showSnack: ShowSnack) {
        getBinding<ActivityMainBinding>()?.mainSnacker?.apply {
            bringToFront()
            reveal(showSnack)
        }
    }
}
