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

//        val query: Query<RecordDao> = recordDao.query().equal(RecordDao.complete, false).build();
//        return recordDao.
//                .getPeople()
//                .doOnSuccess { result: Response<BasePeople?> -> // on success map to db people and insert
//                    val people = result.body()?.results?.map { PeopleDao(id = 0, name = it.name + "cached") }
//                    peopleDao.removeAll()
//                    peopleDao.put(people)
//                }.onErrorReturn { Response.success(getCached()) } // on error we try to get the last cached items
    }


//    Query<Task> query = taskBox.query().equal(Task_.complete, false).build();
//    query.subscribe(subscriptions)
//    .on(AndroidScheduler.mainThread())
//    .observer(data -> updateUi(data ));
//
//    private fun getCached(): BasePeople {
//        return BasePeople(peopleDao
//                .all
//                .filterNotNull()
//                .map { People(it.name, it.height, it.mass, it.hair_color, it.skin_color, it.eye_color, it.gender) })
//    }
}