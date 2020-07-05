package de.ka.jamit.tcalc.repo

import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.repo.db.UserDao
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
    fun addRecord(key: String, value: Float = 0.0f, timeSpan: RecordDao.TimeSpan = RecordDao.TimeSpan.MONTHLY)

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
    fun calc(data: List<RecordDao>): Single<Float>

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


}
