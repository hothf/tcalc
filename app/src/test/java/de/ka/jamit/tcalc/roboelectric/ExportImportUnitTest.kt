package de.ka.jamit.tcalc.roboelectric

import android.os.Build
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import com.opencsv.CSVWriter
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.roboelectric.base.RoboelectricKoinApplication
import de.ka.jamit.tcalc.roboelectric.base.outputStream
import de.ka.jamit.tcalc.utils.CSVUtils

import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


/**
 * Test for exporting and importing functionality.
 *
 * Created by Thomas Hofmann on 16.07.20
 **/
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = RoboelectricKoinApplication::class)
class ExportImportUnitTest : KoinTest {

    private val app = ApplicationProvider.getApplicationContext<RoboelectricKoinApplication>()

    private val csvUtils: CSVUtils by inject()
    private val repository: Repository by inject()

    @Test
    fun `should export correctly`() {
        // given
        val userId = repository.getCurrentlySelectedUser().id
        val newRecords = listOf(RecordDao(id = 0, key = "wat", userId = userId),
                RecordDao(id = 0, key = "wat", userId = userId))
        repository.addRecords(newRecords)

        // first register
        val uri = "content://test/file1.csv".toUri() // uri is not important, it is written to the output stream
        app.appContentResolver?.registerOutputStream(uri, outputStream {
            // then
            println("received something")
        })

        // when, then
        csvUtils.exportCSV(uri).test().assertComplete()
    }

    @Test
    fun `should import correctly`() {
        // given
        // first register
        val uri = "content://test/file1.csv".toUri() // uri is not important, it is read from the resource
        app.appContentResolver?.registerInputStream(uri, this.javaClass.classLoader?.getResourceAsStream("test_import_file.csv"))

        // when, then
        csvUtils.importCSV(uri).test().assertComplete()
    }
}