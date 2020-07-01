package de.ka.jamit.arch.repo

import android.app.Application
import de.ka.jamit.arch.repo.api.ApiService
import de.ka.jamit.arch.repo.api.BasePeople
import de.ka.jamit.arch.repo.api.People
import de.ka.jamit.arch.repo.db.AppDatabase
import de.ka.jamit.arch.repo.db.PeopleDao
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Single
import retrofit2.Response

/**
 * The implementation for the abstraction of data sources.
 */
class RepositoryImpl(val app: Application, val api: ApiService, val db: AppDatabase) : Repository {

    private val peopleDao: Box<PeopleDao> = db.get().boxFor()

    override fun getPeople(): Single<Response<BasePeople?>> {
        return api
                .getPeople()
                .doOnSuccess { result: Response<BasePeople?> -> // on success map to db people and insert
                    val people = result.body()?.results?.map { PeopleDao(id = 0, name = it.name + "cached") }
                    peopleDao.removeAll()
                    peopleDao.put(people)
                }.onErrorReturn { Response.success(getCached()) } // on error we try to get the last cached items
    }

    private fun getCached(): BasePeople {
        return BasePeople(peopleDao
                .all
                .filterNotNull()
                .map { People(it.name, it.height, it.mass, it.hair_color, it.skin_color, it.eye_color, it.gender) })
    }
}