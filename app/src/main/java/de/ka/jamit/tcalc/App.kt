package de.ka.jamit.tcalc

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * App creation point. Please keep as simple as possible - keep an eye on memory leaks: please
 * do not access the app context through a singleton here;
 *
 * The application context is available through injection, use the [de.ka.jamit.tcalc.utils.resources.ResourcesProvider]
 * for these and further resources related methods.
 **/
@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // debug logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}