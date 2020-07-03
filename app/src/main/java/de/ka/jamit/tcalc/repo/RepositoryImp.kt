package de.ka.jamit.tcalc.repo

import android.app.Application
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.RecordDao
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

    init {
        if (recordDao.isEmpty) {
            recordDao.put(db.masterData)
        }
    }

    override fun addRecord(key: String, value: Float) {
        recordDao.put(RecordDao(id = 0, key = key, value = value))
    }

    override fun deleteRecord(id: Long) {
        recordDao.remove(id)
    }

    override fun observeRecords(): Observable<List<RecordDao>> {
        val query: Query<RecordDao> = recordDao.query().build()
        return RxQuery.observable<RecordDao>(query)
    }

    override fun updateRecord(value: Float, id: Long) {
        val existing = recordDao.get(id)
        if (existing != null) {
            recordDao.put(RecordDao(id = id, value = value, key = existing.key))
        }
    }

    override fun calc(data: List<RecordDao>): Single<Float> {
        return Single.fromCallable {
            data.fold(0.0f) { total, item -> total + item.value }
        }
    }
}