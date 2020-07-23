package de.ka.jamit.tcalc.repo

import de.ka.jamit.tcalc.repo.db.*
import io.reactivex.Flowable
import io.reactivex.Single


/**
 * The implementation for the abstraction of data sources.
 */
class RepositoryImpl(val db: AppDatabaseDao) : Repository {

    private var currentlySelectedUser: User? = null
    private var lastRemovedRecordDao: Record? = null
    private var lastRemovedUserDao: User? = null
    private var lastRemovedUserRecords: List<Record>? = null

    override var lastImportResult: Repository.ImportResult? = null

    init {
        insertDefaultValues()
    }

    override fun wipeDatabase() {
        lastRemovedRecordDao = null
        lastRemovedUserDao = null
        lastImportResult = null
        currentlySelectedUser = null

        db.deleteAllRecords()
        db.deleteAllUsers()
        insertDefaultValues()
    }

    private fun insertDefaultValues() {
        if (db.getFirstUser() == null) {
            val id = db.addUser(AppDatabase.getDefaultUser())
            db.addRecords(AppDatabase.getMasterData(id.toInt()))
        }
    }

    override fun observeUsers(): Flowable<List<User>> {
        return db.observeUsers()
    }

    override fun getAllRecordsOfCurrentlySelectedUser(): List<Record> {
        currentlySelectedUser?.id?.let {
            return db.getRecordsOfUser(it)
        }
        return emptyList()
    }

    private fun getDefaultUserId(): Int? {
        val user = db.getFirstUserWithName("default")
        return user?.id
    }

    override fun getCurrentlySelectedUser(): User {
        if (currentlySelectedUser == null) {
            currentlySelectedUser = db.getCurrentlySelectedUser()
        }
        // intentional force unwrap, there has to be a user - always!
        return currentlySelectedUser!!
    }

    override fun selectUser(id: Int) {
        val existing = getCurrentlySelectedUser()
        existing.selected = false
        db.updateUser(existing)

        val selectedUser = db.getUserWithId(id)
        if (selectedUser != null) {
            selectedUser.selected = true
            db.updateUser(selectedUser)
            currentlySelectedUser = selectedUser
        }
    }

    override fun updateUser(id: Int, name: String) {
        val user = db.getUserWithId(id)
        user?.name = name
        if (user != null) {
            db.updateUser(user)

            if (currentlySelectedUser?.id == id){
                currentlySelectedUser = user
            }
        }
    }

    override fun addUser(name: String) {
        val newId = db.addUser(User(name = name))
        selectUser(newId.toInt())
    }

    override fun deleteUser(id: Int) {
        if (currentlySelectedUser?.id == id) { // would be not so nice to have nothing selected
            getDefaultUserId()?.let(::selectUser) // this user is not deletable, so select it now!
        }
        lastRemovedUserDao = db.getUserWithId(id)

        val records = db.getRecordsOfUser(id)
        lastRemovedUserRecords = records
        //recordDao.remove(records) // TODO test this out, it may be that room is intelligent and deletes itself
        db.deleteUserById(id)

    }

    override fun undoDeleteLastUser() {
        val lastUser = lastRemovedUserDao
        if (lastUser != null) {
            db.addUser(lastUser)
            lastRemovedUserDao = null
            val lastRecords = lastRemovedUserRecords
            lastRecords?.let(db::addRecords)
            lastRemovedUserRecords = null
        }
    }

    override fun addRecord(key: String,
                           value: Float,
                           timeSpan: TimeSpan,
                           category: Category,
                           isConsidered: Boolean,
                           isIncome: Boolean) {
        getCurrentlySelectedUser().let {
            db.addRecord(Record(value = value,
                    key = key,
                    timeSpan = timeSpan,
                    category = category,
                    isConsidered = isConsidered,
                    isIncome = isIncome,
                    userId = it.id))
        }
    }

    override fun getDefaultUser(): User? {
        return db.getFirstUserWithName("default")
    }

    override fun addRecords(list: List<Record>) {
        db.addRecords(list)
    }

    override fun updateRecord(value: Float,
                              key: String,
                              timeSpan: TimeSpan,
                              category: Category,
                              isConsidered: Boolean,
                              isIncome: Boolean,
                              id: Int) {
        db.getRecordWithId(id)?.let {
            it.apply {
                it.value = value
                it.key = key
                it.timeSpan = timeSpan
                it.category = category
                it.isConsidered = isConsidered
                it.isIncome = isIncome
                it.userId = it.userId
            }
            db.updateRecord(it)
        }
    }

    override fun deleteRecord(id: Int) {
        lastRemovedRecordDao = db.getRecordWithId(id)
        db.deleteRecordById(id)
    }

    override fun undoDeleteLastRecord() {
        val lastRecord = lastRemovedRecordDao
        if (lastRecord != null && currentlySelectedUser?.id == lastRecord.userId) {
            db.addRecord(lastRecord)
            lastRemovedRecordDao = null
        }
    }

    override fun observeRecordsOfCurrentlySelected(): Flowable<List<Record>> {
        return db.observeRecords(getCurrentlySelectedUser().id)
    }

    override fun calc(data: List<Record>): Single<Repository.CalculationResult> {
        return Single.fromCallable {
            val monthlyOutput = data
                    .filter { it.isConsidered && !it.isIncome }
                    .fold(0.0f) { total, item ->
                        when (item.timeSpan) {
                            TimeSpan.MONTHLY -> total + item.value
                            TimeSpan.QUARTERLY -> total + (item.value / 3)
                            TimeSpan.YEARLY -> total + (item.value / 12)
                        }
                    }
            val monthlyInput = data
                    .filter { it.isConsidered && it.isIncome }
                    .fold(0.0f) { total, item ->
                        when (item.timeSpan) {
                            TimeSpan.MONTHLY -> total + item.value
                            TimeSpan.QUARTERLY -> total + (item.value / 3)
                            TimeSpan.YEARLY -> total + (item.value / 12)
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