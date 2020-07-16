package de.ka.jamit.tcalc.roboelectric.base

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.RepositoryImpl
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.AppDatabaseImpl
import de.ka.jamit.tcalc.utils.CSVUtils
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.KoinContextHandler.getOrNull
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowContentResolver
import java.io.*

/**
 * An application test used with koin.
 *
 * Created by Thomas Hofmann on 16.07.20
 **/
class RoboelectricKoinApplication : Application() {

    var appContentResolver: ShadowContentResolver? = null

    override fun onCreate() {
        super.onCreate()

        // koin initialisation
        if (getOrNull() == null) {
            val testAppModule = module {
                single { AppDatabaseImpl(get()) as AppDatabase }
                single { RepositoryImpl(get()) as Repository }
                single { CSVUtils(get(), get()) }
            }

            startKoin {
                androidLogger()
                androidContext(this@RoboelectricKoinApplication)
                modules(testAppModule)
            }
        }

        // content resolver initialisation
        appContentResolver = shadowOf(this.contentResolver)
    }
}

/**
 * Handy method for assigning an output stream.
 */
fun outputStream(write: (Int) -> Unit): OutputStream {
    return ApplicationOutputStream(write)
}

/**
 * Abstraction for output stream usage.
 */
class ApplicationOutputStream(private val output: (Int) -> Unit) : OutputStream() {
    override fun write(b: Int) {
        output(b)
    }
}