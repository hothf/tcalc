package de.ka.jamit.arch

import androidx.test.runner.AndroidJUnit4
import de.ka.jamit.arch.ui.settings.list.SettingsAdapter
import de.ka.jamit.arch.ui.settings.list.SettingsItemViewModel
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun testItemManipulations() {

        val clickListener: (SettingsItemViewModel) -> Unit = {}

        val adapter = SettingsAdapter(clickListener = clickListener)

        Assert.assertEquals(6, adapter.itemCount)
        Assert.assertEquals("1", adapter.getItems().first().item.title)

        adapter.sort()

        Thread.sleep(500)
        Assert.assertEquals("6", adapter.getItems().first().item.title)
    }
}
