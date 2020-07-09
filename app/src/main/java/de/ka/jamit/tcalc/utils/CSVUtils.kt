package de.ka.jamit.tcalc.utils

import android.app.Application
import android.net.Uri
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.RecordDao
import io.reactivex.Completable
import java.io.InputStreamReader
import java.io.OutputStreamWriter


/**
 * Offers utility functions for handling CSV export and import.
 *
 * Created by Thomas Hofmann on 08.07.20
 **/
class CSVUtils(private val repository: Repository, private val app: Application) {

    /**
     * Imports a csv file into the database. Please consider to have this executed in a non-blocking fashion.
     */
    fun importCSV(uri: Uri): Completable {
        return Completable.create { emitter ->
            try {
                app.contentResolver.openInputStream(uri)?.let { inputStream ->
                    val reader = CSVReader(InputStreamReader(inputStream))
                    val records = mutableListOf<RecordDao>()
                    var record: Array<String?>?

                    var name = app.resources.getString(R.string.import_fallback_name)
                    val cut: Int? = uri.path?.lastIndexOf('/')
                    if (cut != null && cut != -1) {
                        name = uri.path?.substring(cut + 1)?.substringBefore(".") ?: name
                    }
                    repository.addUser(name) // auto selects this user

                    val timeSpanConverter = RecordDao.TimeSpanConverter()
                    while (reader.readNext().also { record = it } != null) {
                        val dao = RecordDao(id = 0,
                                key = record?.get(0) ?: "",
                                timeSpan = timeSpanConverter.convertToEntityProperty(record?.get(1)?.toInt()),
                                value = record?.get(2)?.toFloat() ?: 0.0f,
                                userId = repository.getCurrentlySelectedUser().id)
                        records.add(dao)
                    }
                    reader.close()

                    repository.addRecords(records)

                    repository.lastImportResult = Repository.ImportResult(name, records.size)
                }
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }
    }

    /**
     * Exports a csv file.
     */
    fun exportCSV(uri: Uri): Completable {
        return Completable.create { emitter ->
            try {
                val records = repository.getAllRecordsOfCurrentlySelectedUser()

                app.contentResolver.openOutputStream(uri)?.let {
                    val writer = CSVWriter(OutputStreamWriter(it))
                    val timeSpanConverter = RecordDao.TimeSpanConverter()
                    records.forEach { record ->
                        val data = arrayOf(
                                record.key,
                                timeSpanConverter.convertToDatabaseValue(record.timeSpan).toString(),
                                record.value.toString())
                        writer.writeNext(data);
                    }
                    writer.close()
                }
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }
    }
}