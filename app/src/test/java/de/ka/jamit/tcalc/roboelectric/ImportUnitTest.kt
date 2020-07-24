package de.ka.jamit.tcalc.roboelectric

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.*
import de.ka.jamit.tcalc.di.SchedulerModule
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.utils.CSVUtils
import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import de.ka.jamit.tcalc.utils.schedulers.TestsSchedulerProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowContentResolver
import javax.inject.Inject

/**
 * Test for exporting and importing functionality.
 *
 * Created by Thomas Hofmann on 16.07.20
 **/
@UninstallModules(SchedulerModule::class)
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = HiltTestApplication::class)
class ImportUnitTest {

    private val app = ApplicationProvider.getApplicationContext<Context>()
    private var appContentResolver: ShadowContentResolver? = null

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
    lateinit var csvUtils: CSVUtils

    @Inject
    lateinit var repository: Repository

    @Before
    fun setUp() {
        hiltRule.inject()
        repository.wipeDatabase()

        if (appContentResolver == null) {
            appContentResolver = Shadows.shadowOf(app.contentResolver)
        }
    }

    @Test
    fun `should import correctly`() {
        // given
        // first register
        val uri = "content://test/file1.csv".toUri() // uri is not important, it is read from the resource
        appContentResolver?.registerInputStream(uri, this.javaClass.classLoader?.getResourceAsStream("test_import_file.csv"))

        // when, then
        csvUtils.importCSV(uri).test().awaitCount(1).assertComplete()

        Assert.assertEquals("file1", repository.getCurrentlySelectedUser().name)
        Assert.assertEquals(4, repository.getAllRecordsOfCurrentlySelectedUser().size) // records from file
    }

    @Test
    fun `should fail import`() {
        // given
        // first register
        val uri = "content://test/file2.csv".toUri() // uri is not important, it is read from the resource
        appContentResolver?.registerInputStream(uri, this.javaClass.classLoader?.getResourceAsStream("test_import_corrupt_file.csv"))

        // when, then
        val errors = csvUtils.importCSV(uri).test().awaitCount(1).errors()

        Assert.assertEquals("default", repository.getCurrentlySelectedUser().name) // import should fail so use default
        Assert.assertEquals(7, repository.getAllRecordsOfCurrentlySelectedUser().size) // records from default
        Assert.assertEquals(1, errors.size)
    }

}