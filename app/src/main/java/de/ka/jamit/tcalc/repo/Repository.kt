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
     * Retrieves all records of the currently selected user.
     */
    fun observeRecordsOfCurrentlySelected(): Observable<List<RecordDao>>

    /**
     * Retrieves all records of the currently selected user.
     */
    fun getAllRecordsOfCurrentlySelectedUser(): List<RecordDao>

    /**
     * Saves a new record.
     */
    fun addRecord(key: String,
                  value: Float = 0.0f,
                  timeSpan: RecordDao.TimeSpan = RecordDao.TimeSpan.MONTHLY,
                  category: RecordDao.Category = RecordDao.Category.COMMON,
                  isConsidered: Boolean,
                  isIncome: Boolean)

    /**
     * Adds multiple records at once.
     */
    fun addRecords(list: List<RecordDao>)

    /**
     * Deletes a given record.
     */
    fun deleteRecord(id: Long)

    /**
     * Undo the last record deletion, if possible.
     */
    fun undoDeleteLastRecord()

    /**
     * Updates a record with the given value.
     */
    fun updateRecord(value: Float,
                     key: String,
                     timeSpan: RecordDao.TimeSpan,
                     category: RecordDao.Category,
                     isConsidered: Boolean,
                     isIncome: Boolean,
                     id: Long)

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
     * Updates the given user.
     */
    fun updateUser(id: Long, name: String)

    /**
     * Adds a new user.
     */
    fun addUser(name: String)

    /**
     * Deletes the given user.
     */
    fun deleteUser(id: Long)

    /**
     * Undo the deletion of the last user, if possible.
     */
    fun undoDeleteLastUser()

    /**
     * The calculation result.
     */
    data class CalculationResult(val monthlyOutput: Float,
                                 val yearlyOutput: Float,
                                 val monthlyIncome: Float,
                                 val yearlyIncome: Float,
                                 val monthlyDifference: Float,
                                 val yearlyDifference: Float)

    /**
     * Represents an import result
     */
    data class ImportResult(val name: String, val recordCount: Int)

    /**
     * Deletes all entries of the database.
     */
    fun wipeDatabase()
}
