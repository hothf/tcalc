package de.ka.jamit.tcalc.roboelectric

import android.os.Build
import android.os.Looper
import android.os.Looper.getMainLooper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.roboelectric.base.RoboelectricKoinApplication
import de.ka.jamit.tcalc.ui.home.HomeViewModel
import io.mockk.MockK
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.internal.schedulers.TrampolineScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config


/**
 * Test for home viewModel capabilities.
 *
 * Created by Thomas Hofmann on 17.07.20
 **/
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = RoboelectricKoinApplication::class)
class HomeViewModelUnitTest : KoinTest {

    @Rule
    @JvmField
    val instantTaskExecutorRole = InstantTaskExecutorRule()

    private val app = ApplicationProvider.getApplicationContext<RoboelectricKoinApplication>()

    private val repository: Repository by inject()


    @Test
    fun `home should load correctly`() {
        // given
        val user = repository.getCurrentlySelectedUser() // force initializing of the database
        Assert.assertNotNull(user)

        val viewModel = HomeViewModel()

        val obsi = Observer<String> {
            println("change" + it)
        }

        val records = listOf(RecordDao(id = 0, value = 2.0f, userId = user.id), RecordDao(id = 0, value = 2.0f, userId = user.id), RecordDao(id = 0, value = 2.0f, userId = user.id))

        val adapter = viewModel.adapter
        runBlocking {
            delay(1000)
            repository.addRecords(records)
            viewModel.resultYearlyOutputText.observeForever(obsi)
            delay(1000)
            shadowOf(getMainLooper()).idle()
            // seems to be a threading problem sometimes it does not fail
            Assert.assertEquals(11, adapter.getItems().size)
        }


    }
}