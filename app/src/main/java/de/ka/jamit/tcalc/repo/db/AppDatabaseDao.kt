package de.ka.jamit.tcalc.repo.db

import androidx.room.*
import io.reactivex.Flowable


/**
 * A app database dao.
 */
@Dao
interface AppDatabaseDao {

    @Query("SELECT * FROM USERS LIMIT 1")
    fun getFirstUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: User?): Long

    @Query("DELETE FROM Users")
    fun deleteAllUsers()

    @Query("SELECT * FROM Users")
    fun observeUsers(): Flowable<List<User>>

    @Query("DELETE FROM records")
    fun deleteAllRecords()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRecord(record: Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRecords(records: List<Record>)

    @Query("SELECT * FROM Records WHERE user_id = :userId")
    fun observeRecords(userId: Int): Flowable<List<Record>>

    @Query("SELECT * FROM records WHERE user_id = :userId")
    fun getRecordsOfUser(userId: Int?): List<Record>

    @Query("SELECT * FROM users WHERE name = :username ORDER BY id ASC LIMIT 1")
    fun getFirstUserWithName(username: String): User?

    @Query("SELECT * FROM users WHERE selected = 1 LIMIT 1")
    fun getCurrentlySelectedUser(): User?

    @Query("SELECT * FROM users WHERE id = :id ORDER BY id ASC LIMIT 1")
    fun getUserWithId(id: Int): User?

    @Query("SELECT * FROM records WHERE id = :id ORDER BY id ASC LIMIT 1")
    fun getRecordWithId(id: Int): Record?

    @Update
    fun updateUser(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    fun deleteUserById(userId: Int)

    @Update
    fun updateRecord(record: Record)

    @Query("DELETE FROM records WHERE id = :id")
    fun deleteRecordById(id: Int)
}