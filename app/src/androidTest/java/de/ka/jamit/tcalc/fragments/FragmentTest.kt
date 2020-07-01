package de.ka.jamit.tcalc.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers

/**
 * Template class to implement fragment scenario tests.
 */
abstract class FragmentTest {

    abstract var displayedViewsInFragment: List<Int>

    /**
     * Launch fragment scenario.
     */
    fun <T : Fragment> launchFragmentScenario(fragmentClass: Class<T>, fragmentArgs: Bundle?): FragmentScenario<*> {
        return FragmentScenario.launchInContainer(fragmentClass, fragmentArgs)
    }

    /**
     * Launch fragment over activity.
     * If fragment needs reference of parent activity use activity scenario and navigate to fragment.
     */
    fun <T : Fragment, E : Activity> launchFragmentOverActivityScenario(activityClass: Class<E>,
                                                                        fragmentClass: Class<T>,
                                                                        fragmentArgs: Bundle?): ActivityScenario<*> {
        val activityScenario = ActivityScenario.launch(activityClass)

        activityScenario.onActivity {
            //navigate to fragment as your app implements it
        }

        return activityScenario
    }

    /**
     * Tests if given views are visible.
     */
    fun testFragmentViewsVisibility(){
        for (id in displayedViewsInFragment) {
            Espresso.onView(ViewMatchers.withId(id)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

}