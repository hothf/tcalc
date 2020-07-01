package de.ka.jamit.tcalc

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.ka.jamit.tcalc.databinding.ItemSettingsBinding
import de.ka.jamit.tcalc.fragments.DetailFragmentTest
import de.ka.jamit.tcalc.fragments.HomeFragmentTest
import de.ka.jamit.tcalc.fragments.ProfileFragmentTest
import de.ka.jamit.tcalc.fragments.SettingsFragmentTest
import de.ka.jamit.tcalc.ui.main.MainActivity
import de.ka.jamit.tcalc.ui.settings.list.SettingItemViewHolder
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example class for testing navigation flows in an activity.
 *
 * To test a bottom navigation supply the menu item ids and the corresponding fragment test class.
 *
 * Provide test cases for navigation flows. To keep it clean limit the view actions
 * to the ones necessary for navigating to a certain fragment. Put more extensive tests in
 * the corresponding fragment test class.
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    private val activity = MainActivity::class.java

    private val bottomNavigationItems = mapOf(
            Pair(R.id.settingsFragment, SettingsFragmentTest()),
            Pair(R.id.profileFragment, ProfileFragmentTest()),
            Pair(R.id.homeFragment, HomeFragmentTest()))

    @Test
    fun testBottomNavigation() {

        var activityScenario = ActivityScenario.launch(activity)

        for(item in bottomNavigationItems){
            onView(withId(item.key)).perform(click())
            item.value.testFragmentViewsVisibility()
            //Add assertions for bottom navigation view layout
            //e.g. selected item different color etc.
        }
    }

    @Test
    fun testNavigationToDetailFragment(){
        var activityScenario = ActivityScenario.launch(activity)
        onView(withId(R.id.settingsFragment)).perform(click())

        SettingsFragmentTest().testFragmentViewsVisibility()
        onView(withId(R.id.recyclerSettings))
                .perform(RecyclerViewActions.actionOnItemAtPosition<SettingItemViewHolder<ItemSettingsBinding>>(0, click()))

        DetailFragmentTest().testFragmentViewsVisibility()
    }

    @Test
    fun testNewNavigationPath(){
        //TODO: Add your navigation paths
    }
}