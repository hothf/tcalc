package de.ka.jamit.tcalc.roboelectric

import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.*
import de.ka.jamit.tcalc.di.SchedulerModule
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.roboelectric.base.outputStream
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
import java.io.ByteArrayInputStream
import java.util.concurrent.CountDownLatch
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
class ExportUnitTest {

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
    fun `should export correctly`() {
        // given
        val countDownLatch = CountDownLatch(1)
        repository.getCurrentlySelectedUser().id // force database to be initialized
        // the default user has 7 values, these are to be exported

        // first register
        val uri = "content://test/file1.csv".toUri() // uri is not important, it is written to the output stream
        appContentResolver?.registerOutputStream(uri, outputStream { stream ->

            Handler().post {
                // then
                val inputStream = ByteArrayInputStream(stream)
                val records = csvUtils.streamRecords(inputStream)
                Assert.assertEquals(7, records.size) // the default entries
                countDownLatch.countDown()
            }
        })

        // when
        Handler().post {
            csvUtils.exportCSV(uri).test()
        }

        countDownLatch.await()
    }
}