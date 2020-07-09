package de.ka.jamit.tcalc.repo

import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.repo.db.UserDao
import io.reactivex.Observable
import io.reactivex.Single

/**
 * The interface for the abstraction of the data sources of the app.
 */
interface Repository {

    var lastImportResult: ImportResult?

    /**
     * Retrieves all records
     */
    fun observeRecords(): Observable<List<RecordDao>>

    /**
     * Retrieves all records of the currently selected user.
     */
    fun getAllRecordsOfCurrentlySelectedUser(): List<RecordDao>

    /**
     * Saves a new record
     */
    fun addRecord(key: String, value: Float = 0.0f, timeSpan: RecordDao.TimeSpan = RecordDao.TimeSpan.MONTHLY)

    /**
     * Adds multiple records at once.
     */
    fun addRecords(list: List<RecordDao>)

    /**
     * Deletes a given record.
     */
    fun deleteRecord(id: Long)

    /**
     * Updates a record with the given value.
     */
    fun updateRecord(value: Float, key: String, timeSpan: RecordDao.TimeSpan, id: Long)

    /**
     * Calculates the sum of the data.
     */
    fun calc(data: List<RecordDao>): Single<CalculationResult>

    /**
     * Retrieves all users
     */
    fun observeUsers(): Observable<List<UserDao>>

    /**
     * Retrieves the currently selected user.
     */
    fun getCurrentlySelectedUser(): UserDao

    /**
     * Selects the given user.
     */
    fun selectUser(id: Long)

    /**
     * Updates the given user
     */
    fun updateUser(id: Long, name: String, selected: Boolean)

    /**
     * Adds a new user.
     */
    fun addUser(name: String)

    /**
     * Deletes the given user.
     */
    fun deleteUser(id: Long)

    /**
     * The calculation result.
     */
    data class CalculationResult(val monthlyValue: Float, val yearlyValue: Float)

    /**
     * Represents an import result
     */
    data class ImportResult(val name: String, val recordCount: Int)


}
