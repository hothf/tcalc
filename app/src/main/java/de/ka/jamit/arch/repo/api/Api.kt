package de.ka.jamit.arch.repo.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET

/**
 * Interface for all possible api calls.
 */
interface Api {

    /**
     * Retrieves an exemplary snippet.
     */
    @GET("people")
    fun getPeople(): Single<Response<BasePeople?>>
}