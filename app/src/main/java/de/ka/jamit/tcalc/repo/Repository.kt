package de.ka.jamit.tcalc.repo

import de.ka.jamit.tcalc.repo.db.Category
import de.ka.jamit.tcalc.repo.db.Record
import de.ka.jamit.tcalc.repo.db.TimeSpan
import de.ka.jamit.tcalc.repo.db.User
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * The interface for the abstraction of the data sources of the app.
 */
interface Repository {

    var lastImportResult: ImportResult?

    /**
     * Retrieves all records of the currently selected user.
     */
    fun observeRecordsOfCurrentlySelected(): Flowable<List<Record>>

    /**
     * Retrieves all records of the currently selected user.
     */
    fun getAllRecordsOfCurrentlySelectedUser(): List<Record>

    /**
     * Saves a new record.
     */
    fun addRecord(key: String,
                  value: Float = 0.0f,
                  timeSpan: TimeSpan = TimeSpan.MONTHLY,
                  category: Category =Category.COMMON,
                  isConsidered: Boolean,
                  isIncome: Boolean)

    /**
     * Adds multiple records at once.
     */
    fun addRecords(list: List<Record>)

    /**
     * Deletes a given record.
     */
    fun deleteRecord(id: Int)

    /**
     * Undo the last record deletion, if possible.
     */
    fun undoDeleteLastRecord()

    /**
     * Updates a record with the given value.
     */
    fun updateRecord(value: Float,
                     key: String,
                     timeSpan: TimeSpan,
                     category: Category,
                     isConsidered: Boolean,
                     isIncome: Boolean,
                     id: Int)

    /**
     * Calculates the sum of the data.
     */
    fun calc(data: List<Record>): Single<CalculationResult>

    /**
     * Retrieves all users
     */
    fun observeUsers(): Flowable<List<User>>

    /**
     * Retrieves the currently selected user.
     */
    fun getCurrentlySelectedUser(): User

    /**
     * Selects the given user.
     */
    fun selectUser(id: Int)

    /**
     * Updates the given user.
     */
    fun updateUser(id: Int, name: String)

    /**
     * Adds a new user.
     */
    fun addUser(name: String)

    /**
     * Deletes the given user.
     */
    fun deleteUser(id: Int)

    /**
     * Undo the deletion of the last user, if possible.
     */
    fun undoDeleteLastUser()

    /**
     * Retrieves the default user, if available
     */
    fun getDefaultUser(): User?

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