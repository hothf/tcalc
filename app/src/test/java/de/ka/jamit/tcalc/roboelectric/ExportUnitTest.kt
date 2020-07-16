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

    private val countDownLatch = CountDownLatch(1)

    @Test
    fun `should export correctly`() {
        // given
        repository.getCurrentlySelectedUser().id // force database to be initialized
        // the default user has 7 values, these are to be exported

        // first register
        val uri = "content://test/file1.csv".toUri() // uri is not important, it is written to the output stream
        app.appContentResolver?.registerOutputStream(uri, outputStream { stream ->

            Handler().post {
                // then
                val inputStream = ByteArrayInputStream(stream)
                val reader = CSVReader(InputStreamReader(inputStream))
                var record: Array<String?>?
                val records = mutableListOf<RecordDao>()
                val timeSpanConverter = RecordDao.TimeSpanConverter()
                val categoryConverter = RecordDao.CategoryConverter()
                while (reader.readNext().also { record = it } != null) {
                    val dao = RecordDao(id = 0,
                            key = record?.get(0) ?: "",
                            timeSpan = timeSpanConverter.convertToEntityProperty(record?.get(1)?.toInt()),
                            category = categoryConverter.convertToEntityProperty(record?.get(2)?.toInt()),
                            isConsidered = record?.get(3)?.toBoolean() ?: true,
                            isIncome = record?.get(4)?.toBoolean() ?: false,
                            value = record?.get(5)?.toFloat() ?: 0.0f,
                            userId = repository.getCurrentlySelectedUser().id)
                    records.add(dao)
                }
                reader.close()
                Assert.assertEquals(7, records.size)
                countDownLatch.countDown()
            }
        })

        // when, then
        Handler().post {
            csvUtils.exportCSV(uri).test()
        }

        countDownLatch.await()
    }

    // due to a parallelism bug we can not have more than one roboelectric test at once running when testing this

}