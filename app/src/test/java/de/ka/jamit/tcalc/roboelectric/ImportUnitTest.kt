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
import java.io.BufferedReader
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
class ImportUnitTest : KoinTest {

    private val app = ApplicationProvider.getApplicationContext<RoboelectricKoinApplication>()

    private val csvUtils: CSVUtils by inject()
    private val repository: Repository by inject()

    private val countDownLatch = CountDownLatch(1)

    @Test
    fun `should import correctly`() {
        // given
        // first register
        val uri = "content://test/file1.csv".toUri() // uri is not important, it is read from the resource
        app.appContentResolver?.registerInputStream(uri, this.javaClass.classLoader?.getResourceAsStream("test_import_file.csv"))

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
        app.appContentResolver?.registerInputStream(uri, this.javaClass.classLoader?.getResourceAsStream("test_import_corrupt_file.csv"))


        // when, then
        val errors = csvUtils.importCSV(uri).test().awaitCount(1).errors()

        Assert.assertEquals("default", repository.getCurrentlySelectedUser().name) // import should fail so use default
        Assert.assertEquals(7, repository.getAllRecordsOfCurrentlySelectedUser().size) // records from default
        Assert.assertEquals(1, errors.size)
    }

}