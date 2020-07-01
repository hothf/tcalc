package de.ka.jamit.arch.repo

import de.ka.jamit.arch.repo.api.BasePeople
import io.reactivex.Single
import retrofit2.Response

/**
 * The interface for the abstraction of the data sources of the app.
 */
interface Repository {

    /**
     * Retrieves people. If there are some cached, they will be retrieved on errors, like no internet.
     */
    fun getPeople(): Single<Response<BasePeople?>>
}
