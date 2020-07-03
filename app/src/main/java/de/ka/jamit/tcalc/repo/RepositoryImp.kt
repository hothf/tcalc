package de.ka.jamit.tcalc.repo

import android.app.Application
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.RecordDao
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.query.Query

import io.objectbox.rx.RxQuery
import io.reactivex.Observable


/**
 * The implementation for the abstraction of data sources.
 */
class RepositoryImpl(val app: Application, val db: AppDatabase) : Repository {

    private val recordDao: Box<RecordDao> = db.get().boxFor()

    override fun saveRecord(newRecord: RecordDao) {
        recordDao.put(newRecord)
    }

    override fun observeRecords(): Observable<List<RecordDao>> {
        val query: Query<RecordDao> = recordDao.query().build()
        return RxQuery.observable<RecordDao>(query)
    }

    override fun updateRecord(value: Float, key: Long) {
        val existing = recordDao.get(key)
        if (existing != null) {
            recordDao.put(RecordDao(id = key, value = value, key = existing.key))
        }
    }
}