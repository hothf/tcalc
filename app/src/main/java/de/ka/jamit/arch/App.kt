package de.ka.jamit.arch

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * App creation point. Please keep as simple as possible - keep an eye on memory leaks: please
 * do not access the app context through a singleton here;
 *
 * The application context is available through injection, use the [de.ka.jamit.arch.utils.resources.ResourcesProvider]
 * for these and further resources related methods.
 **/
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // start injecting with koin
        startKoin{
            androidContext(this@App)
            modules(appModule)
        }

        // debug logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }


    }

}