package de.ka.jamit.tcalc.roboelectric

import android.os.Build
import android.os.Handler
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import com.opencsv.CSVReader
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.roboelectric.base.RoboelectricKoinApplication
import de.ka.jamit.tcalc.roboelectric.base.outputStream
import de.ka.jamit.tcalc.utils.CSVUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.concurrent.CountDownLatch


/**
 * Test for exporting and importing functionality.
 *
 * Created by Thomas Hofmann on 16.07.20
 **/
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = RoboelectricKoinApplication::class)
class ExportUnitTest : KoinTest {

    private val app = ApplicationProvider.getApplicationContext<RoboelectricKoinApplication>()

    private val csvUtils: CSVUtils by inject()
    private val repository: Repository by inject()


    @Test
    fun `should export correctly`() {
        // given
        val countDownLatch = CountDownLatch(1)
        repository.getCurrentlySelectedUser().id // force database to be initialized
        // the default user has 7 values, these are to be exported

        // first register
        val uri = "content://test/file1.csv".toUri() // uri is not important, it is written to the output stream
        app.appContentResolver?.registerOutputStream(uri, outputStream { stream ->

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