package de.ka.jamit.tcalc.repo

import de.ka.jamit.tcalc.repo.db.RecordDao
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import retrofit2.Response

/**
 * The interface for the abstraction of the data sources of the app.
 */
interface Repository {

    /**
     * Retrieves all records
     */
    fun observeRecords(): Observable<List<RecordDao>>

    /**
     * Saves a new record
     */
    fun addRecord(key: String, value: Float = 0.0f)

    /**
     * Deletes a given record.
     */
    fun deleteRecord(id: Long)

    /**
     * Updates a record with the given value.
     */
    fun updateRecord(value: Float, id: Long)


}
