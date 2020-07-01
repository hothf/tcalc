package de.ka.jamit.tcalc.repo.api

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.ka.jamit.tcalc.BuildConfig
import de.ka.jamit.tcalc.utils.gson.NullableStringTypeAdapter
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.KoinComponent
import org.threeten.bp.Instant
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * A service for making api calls.
 */
class ApiService(val app: Application) : KoinComponent {

    //
    // Setup
    //

    private val api: Api by lazy {
        buildApi()
    }

    private fun buildApi(): Api {
        val gson: Gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Instant::class.java, NullableStringTypeAdapter(Instant::toString, Instant::parse))
                .registerTypeAdapter(HttpUrl::class.java, NullableStringTypeAdapter(HttpUrl::toString, HttpUrl::get))
                .create()

        val retrofit = Retrofit.Builder()
                .client(buildOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .baseUrl(BuildConfig.API_URL)
                .build()

        return retrofit.create(Api::class.java)
    }

    private fun buildOkHttpClient() = OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Timber.e(it) }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    //
    // Actual api calls
    //

    /**
     * Retrieves an exemplary  snippet.
     */
    fun getPeople() = api.getPeople()
}