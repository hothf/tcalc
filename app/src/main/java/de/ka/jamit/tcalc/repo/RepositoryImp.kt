package de.ka.jamit.tcalc.repo

import android.app.Application
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.repo.db.RecordDao_
import de.ka.jamit.tcalc.repo.db.UserDao
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

import io.objectbox.rx.RxQuery
import io.reactivex.Observable
import io.reactivex.Single


/**
 * The implementation for the abstraction of data sources.
 */
class RepositoryImpl(val app: Application, val db: AppDatabase) : Repository {

    private val recordDao: Box<RecordDao> = db.get().boxFor()
    private val userDao: Box<UserDao> = db.get().boxFor()
    private var currentlySelectedUser: UserDao? = null

    override var lastImportResult: Repository.ImportResult? = null

    init {
        if (userDao.isEmpty) {
            userDao.put(db.defaultUser)
        }
        if (recordDao.isEmpty) {
            recordDao.put(db.masterData)
        }
    }

    override fun observeUsers(): Observable<List<UserDao>> {
        val query: Query<UserDao> = userDao.query().build()
        return RxQuery.observable<UserDao>(query)
    }

    override fun getAllRecordsOfCurrentlySelectedUser(): List<RecordDao> {
        currentlySelectedUser?.id?.let {
            return recordDao.query()
                    .equal(RecordDao_.userId, it)
                    .build()
                    .find()
                    .toList()
        }
        return emptyList()
    }

    override fun getCurrentlySelectedUser(): UserDao {
        if (currentlySelectedUser == null) {
            currentlySelectedUser = userDao.all.find { it.selected }
        }
        // intentional force unwrap, there has to be a user - always!
        return currentlySelectedUser!!
    }

    override fun selectUser(id: Long) {
        val existing = getCurrentlySelectedUser()
        updateUser(existing.id, existing.name, false)

        val selectedUser = userDao.get(id)
        if (selectedUser != null) {
            updateUser(id, selectedUser.name, true)
            currentlySelectedUser = selectedUser
        }
    }

    override fun updateUser(id: Long, name: String, selected: Boolean) {
        userDao.get(id)?.let {
            val user = UserDao(id = id, name = name, selected = selected)
            userDao.put(user)
            if (id == currentlySelectedUser?.id) {
                currentlySelectedUser = user
            }
        }
    }

    override fun addUser(name: String) {
        val newId = userDao.put(UserDao(0, name, false))
        selectUser(newId)
    }

    override fun deleteUser(id: Long) {
        if (currentlySelectedUser?.id == id) { // would be not so nice to have nothing selected
            selectUser(1) // user 1 is not deletable, so select it now!
        }
        val recordIds = recordDao.query()
                .equal(RecordDao_.userId, id)
                .build()
                .findIds()
                .toList()
        recordDao.removeByIds(recordIds)
        userDao.remove(id)
    }

    override fun addRecord(key: String,
                           value: Float,
                           timeSpan: RecordDao.TimeSpan,
                           category: RecordDao.Category,
                           isConsidered: Boolean,
                           isIncome: Boolean) {
        getCurrentlySelectedUser().let {
            recordDao.put(RecordDao(
                    id = 0,
                    value = value,
                    key = key,
                    timeSpan = timeSpan,
                    category = category,
                    isConsidered = isConsidered,
                    isIncome = isIncome,
                    userId = it.id))
        }
    }

    override fun addRecords(list: List<RecordDao>) {
        recordDao.put(list)
    }

    override fun updateRecord(value: Float,
                              key: String,
                              timeSpan: RecordDao.TimeSpan,
                              category: RecordDao.Category,
                              isConsidered: Boolean,
                              isIncome: Boolean,
                              id: Long) {
        recordDao.get(id)?.let {
            recordDao.put(RecordDao(
                    id = id,
                    value = value,
                    key = key,
                    timeSpan = timeSpan,
                    category = category,
                    isConsidered = isConsidered,
                    isIncome = isIncome,
                    userId = it.userId))
        }
    }

    override fun deleteRecord(id: Long) {
        recordDao.remove(id)
    }

    override fun observeRecords(): Observable<List<RecordDao>> {
        val query: Query<RecordDao> = recordDao.query().equal(RecordDao_.userId, getCurrentlySelectedUser().id).build()
        return RxQuery.observable<RecordDao>(query)
    }

    override fun calc(data: List<RecordDao>): Single<Repository.CalculationResult> {
        return Single.fromCallable {
            val monthly = data
                    .filter { it.isConsidered }
                    .fold(0.0f) { total, item ->
                        when (item.timeSpan) {
                            RecordDao.TimeSpan.MONTHLY -> total + item.value
                            RecordDao.TimeSpan.QUARTERLY -> total + (item.value / 4)
                            RecordDao.TimeSpan.YEARLY -> total + (item.value / 12)
                        }
                    }
            Repository.CalculationResult(monthly, monthly * 12)
        }
    }
}