package de.ka.jamit.arch

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.ka.jamit.arch.ui.main.MainActivity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example class for testing activity lifecycle states.
 *
 * https://developer.android.com/guide/components/activities/testing
 */
@RunWith(AndroidJUnit4::class)
class ActivityLifecycleTest {

    private val activity = MainActivity::class.java

    @Test
    fun createActivity(){
        val activityScenario = ActivityScenario.launch(activity)
        Assert.assertEquals(activityScenario.state, Lifecycle.State.RESUMED)
        //Add assertions for state resumed
    }

    /**
     * This test simulates when app is killed by a system process and is then recreated.
     */
    @Test
    fun recreateActivity(){
        val activityScenario = ActivityScenario.launch(activity)
        Assert.assertEquals(activityScenario.state, Lifecycle.State.RESUMED)
        activityScenario.recreate()
        Assert.assertEquals(activityScenario.state, Lifecycle.State.RESUMED)
        //Add assertions for state resumed
    }

    @Test
    fun testStateCreated(){
        val activityScenario = ActivityScenario.launch(activity)
        activityScenario.moveToState(Lifecycle.State.CREATED)
        Assert.assertEquals(activityScenario.state, Lifecycle.State.CREATED)
        //Add assertions for state created
    }

    @Test
    fun testStateStarted(){
        val activityScenario = ActivityScenario.launch(activity)
        activityScenario.moveToState(Lifecycle.State.STARTED)
        Assert.assertEquals(activityScenario.state, Lifecycle.State.STARTED)
        //Add assertions for state started
    }

    @Test
    fun testStateResumed(){
        val activityScenario = ActivityScenario.launch(activity)
        activityScenario.moveToState(Lifecycle.State.RESUMED)
        Assert.assertEquals(activityScenario.state, Lifecycle.State.RESUMED)
        //Add assertions for state resumed
    }

    @Test
    fun testStateDestroyed(){
        val activityScenario = ActivityScenario.launch(activity)
        activityScenario.moveToState(Lifecycle.State.DESTROYED)
        Assert.assertEquals(activityScenario.state, Lifecycle.State.DESTROYED)
        //Add assertions for state destroyed
    }
}