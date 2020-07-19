package de.ka.jamit.tcalc.repo

import de.ka.jamit.tcalc.repo.db.*
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

import io.objectbox.rx.RxQuery
import io.reactivex.Observable
import io.reactivex.Single


/**
 * The implementation for the abstraction of data sources.
 */
class RepositoryImpl(val db: AppDatabase) : Repository {

    private val recordDao: Box<RecordDao> = db.get().boxFor()
    private val userDao: Box<UserDao> = db.get().boxFor()
    private var currentlySelectedUser: UserDao? = null
    private var lastRemovedRecordDao: RecordDao? = null
    private var lastRemovedUserDao: UserDao? = null
    private var lastRemovedUserRecords: List<RecordDao>? = null

    override var lastImportResult: Repository.ImportResult? = null

    init {
        insertDefaultValues()
    }

    override fun wipeDatabase() {
        lastRemovedRecordDao = null
        lastRemovedUserDao = null
        lastImportResult = null
        currentlySelectedUser = null
        userDao.removeAll()
        recordDao.removeAll()
        insertDefaultValues()
    }

    private fun insertDefaultValues() {
        if (userDao.isEmpty) {
            val userId = userDao.put(db.getDefaultUser())
            recordDao.put(db.getMasterData(userId))
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

    private fun getDefaultUserId(): Long? {
        val user = userDao
                .query()
                .equal(UserDao_.name, "default")
                .build()
                .findFirst()
        return user?.id
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
        updateUserInternally(existing.id, existing.name, false)

        val selectedUser = userDao.get(id)
        if (selectedUser != null) {
            updateUserInternally(id, selectedUser.name, true)
            currentlySelectedUser = selectedUser
        }
    }

    override fun updateUser(id: Long, name: String) {
        userDao.get(id)?.let {
            updateUserInternally(it.id, name, it.selected)
        }
    }

    private fun updateUserInternally(id: Long, name: String, selected: Boolean) {
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
            getDefaultUserId()?.let(::selectUser) // this user is not deletable, so select it now!
        }
        lastRemovedUserDao = userDao.get(id)
        val records = recordDao.query()
                .equal(RecordDao_.userId, id)
                .build()
                .find()
        lastRemovedUserRecords = records
        recordDao.remove(records)
        userDao.remove(id)
    }

    override fun undoDeleteLastUser() {
        val lastUser = lastRemovedUserDao
        if (lastUser != null) {
            userDao.put(lastUser)
            lastRemovedUserDao = null
            val lastRecords = lastRemovedUserRecords
            lastRecords?.let(recordDao::put)
            lastRemovedUserRecords = null
        }
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
        lastRemovedRecordDao = recordDao.get(id)
        recordDao.remove(id)
    }

    override fun undoDeleteLastRecord() {
        val lastRecord = lastRemovedRecordDao
        if (lastRecord != null && currentlySelectedUser?.id == lastRecord.userId) {
            recordDao.put(lastRecord)
            lastRemovedRecordDao = null
        }
    }

    override fun observeRecordsOfCurrentlySelected(): Observable<List<RecordDao>> {
        val query: Query<RecordDao> = recordDao.query().equal(RecordDao_.userId, getCurrentlySelectedUser().id).build()
        return RxQuery.observable<RecordDao>(query)
    }

    override fun calc(data: List<RecordDao>): Single<Repository.CalculationResult> {
        return Single.fromCallable {
            val monthlyOutput = data
                    .filter { it.isConsidered && !it.isIncome }
                    .fold(0.0f) { total, item ->
                        when (item.timeSpan) {
                            RecordDao.TimeSpan.MONTHLY -> total + item.value
                            RecordDao.TimeSpan.QUARTERLY -> total + (item.value / 3)
                            RecordDao.TimeSpan.YEARLY -> total + (item.value / 12)
                        }
                    }
            val monthlyInput = data
                    .filter { it.isConsidered && it.isIncome }
                    .fold(0.0f) { total, item ->
                        when (item.timeSpan) {
                            RecordDao.TimeSpan.MONTHLY -> total + item.value
                            RecordDao.TimeSpan.QUARTERLY -> total + (item.value / 3)
                            RecordDao.TimeSpan.YEARLY -> total + (item.value / 12)
                        }
                    }
            Repository.CalculationResult(
                    monthlyOutput = monthlyOutput,
                    yearlyOutput = monthlyOutput * 12,
                    monthlyIncome = monthlyInput,
                    yearlyIncome = monthlyInput * 12,
                    monthlyDifference = (monthlyInput - monthlyOutput),
                    yearlyDifference = (monthlyInput - monthlyOutput) * 12)
        }
    }
}