package de.ka.jamit.arch.fragments

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.ka.jamit.arch.R
import de.ka.jamit.arch.ui.profile.ProfileFragment
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example class for testing the layout changes in a fragment.
 *
 * The fragment class and layout ids of views which should be displayed on fragment start
 * can be set at the top. If the fragment expects arguments, supply them as Bundle.
 *
 * If your fragment contains layout changes in a specific lifecycle state,
 * add your tests after moving the scenario to the specific state.
 *
 * To test if fragment views are displayed correctly after the fragment has been
 * killed by the system and was recreated, use   scenario.recreate().
 *
 * Add new test cases for specific layout changes, which are triggered during user interactions.
 *
 * https://developer.android.com/training/basics/fragments/testing
 */
@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest : FragmentTest(){

    /**
     * Edit these arguments according to the fragment you want to test.
     */
    private val fragmentClass = ProfileFragment::class.java
    private val fragmentArgs = Bundle().apply {}
    override var displayedViewsInFragment = listOf(R.id.profile_snack_button)

    @Test
    fun createFragment() {
        val scenario = launchFragmentScenario(fragmentClass,fragmentArgs)
        //state resumed
        testFragmentViewsVisibility()
    }

    @Test
    fun lifecycleStates(){
        val scenario = launchFragmentScenario(fragmentClass, fragmentArgs)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.STARTED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.moveToState(Lifecycle.State.DESTROYED)

        //Add assertions after the states or write new lifecycle test functions
    }

    @Test
    fun newTestCase(){
        //TODO: Add your test cases
    }
}