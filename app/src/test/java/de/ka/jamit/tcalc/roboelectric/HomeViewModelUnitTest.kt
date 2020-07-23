package de.ka.jamit.tcalc.roboelectric

import android.os.Build
import android.os.Looper.getMainLooper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.testing.*
import de.ka.jamit.tcalc.di.SchedulerModule
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.Record
import de.ka.jamit.tcalc.ui.home.HomeViewModel
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import de.ka.jamit.tcalc.utils.schedulers.TestsSchedulerProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import javax.inject.Inject


/**
 * Test for home viewModel capabilities.
 *
 * Created by Thomas Hofmann on 17.07.20
 **/
@UninstallModules(SchedulerModule::class)
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = HiltTestApplication::class)
class HomeViewModelUnitTest {

    @Rule
    @JvmField
    var hiltRule = HiltAndroidRule(this)

    @Rule
    @JvmField
    val instantTaskExecutorRole = InstantTaskExecutorRule() // important for RX tests to return immediately

    @BindValue
    @JvmField
    val schedulerProvider: SchedulerProvider = TestsSchedulerProvider() // swap the scheduler provider for RX

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var resourcesProvider: ResourcesProvider

    @Before
    fun setUp() {
        hiltRule.inject()
        repository.wipeDatabase()
    }

    @Test
    fun `home should load correctly`() {
        // given
        val user = repository.getCurrentlySelectedUser() // force initializing of the database
        Assert.assertNotNull(user)

        val viewModel = HomeViewModel(SavedStateHandle(), repository, schedulerProvider, resourcesProvider)


        runBlocking {
            delay(50)
            shadowOf(getMainLooper()).idle()

            // then
            // we have 7 default items, add 1 for a more button, this should make 8:
            Assert.assertEquals(8, viewModel.adapter.getItems().size)
        }
    }

    @Test
    fun `home should calc correctly`() {
        // given
        val user = repository.getCurrentlySelectedUser() // force initializing of the database
        Assert.assertNotNull(user)

        val viewModel = HomeViewModel(SavedStateHandle(), repository, schedulerProvider, resourcesProvider)

        val yearlyOutputObserver = Observer<String> {
            Assert.assertEquals("48.00 €", it)
        }
        val monthlyOutputObserver = Observer<String> {
            Assert.assertEquals("4.00 €", it)
        }
        val yearlyDeltaObserver = Observer<String> {
            Assert.assertEquals("-24.00 €", it)
        }
        val monthlyDeltaObserver = Observer<String> {
            Assert.assertEquals("-2.00 €", it)
        }
        val yearlyIncomeObserver = Observer<String> {
            Assert.assertEquals("24.00 €", it)
        }
        val monthlyIncomeObserver = Observer<String> {
            Assert.assertEquals("2.00 €", it)
        }

        val records = listOf(Record(id = 0, value = 2.0f, userId = user.id),
                Record(id = 0, value = 2.0f, userId = user.id),
                Record(id = 0, value = 2.0f, isIncome = true, userId = user.id))

        runBlocking {
            repository.addRecords(records)
            delay(50)
            shadowOf(getMainLooper()).idle()

            // then
            // we have 7 default items, add 1 for a more button and add 3 in this test, this should make 11:
            Assert.assertEquals(11, viewModel.adapter.getItems().size)

            // see the observers if calculated correctly
            viewModel.resultYearlyOutputText.observeForever(yearlyOutputObserver)
            viewModel.resultMonthlyOutputText.observeForever(monthlyOutputObserver)
            viewModel.resultYearlyDeltaText.observeForever(yearlyDeltaObserver)
            viewModel.resultMonthlyDeltaText.observeForever(monthlyDeltaObserver)
            viewModel.resultYearlyIncomeText.observeForever(yearlyIncomeObserver)
            viewModel.resultMonthlyIncomeText.observeForever(monthlyIncomeObserver)
        }
    }
}