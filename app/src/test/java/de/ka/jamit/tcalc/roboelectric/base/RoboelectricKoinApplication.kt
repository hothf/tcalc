package de.ka.jamit.tcalc.roboelectric.base

import android.app.Application
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.RepositoryImpl
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.AppDatabaseImpl
import de.ka.jamit.tcalc.ui.home.HomeViewModel
import de.ka.jamit.tcalc.ui.home.addedit.HomeAddEditDialogViewModel
import de.ka.jamit.tcalc.ui.home.category.CategoryDialogViewModel
import de.ka.jamit.tcalc.ui.home.user.UserDialogViewModel
import de.ka.jamit.tcalc.ui.home.user.addedit.UserAddEditDialogViewModel
import de.ka.jamit.tcalc.ui.main.MainViewModel
import de.ka.jamit.tcalc.ui.settings.SettingsViewModel
import de.ka.jamit.tcalc.ui.settings.exporting.ExportingDialogViewModel
import de.ka.jamit.tcalc.ui.settings.importing.ImportingDialogViewModel
import de.ka.jamit.tcalc.utils.CSVUtils
import de.ka.jamit.tcalc.utils.CloseEventListener
import de.ka.jamit.tcalc.utils.GlobalMessageEventListener
import de.ka.jamit.tcalc.utils.InputValidator
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import de.ka.jamit.tcalc.utils.resources.ResourcesProviderImpl
import de.ka.jamit.tcalc.utils.schedulers.AndroidSchedulerProvider
import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import de.ka.jamit.tcalc.utils.schedulers.TestsSchedulerProvider
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
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
                single { GlobalMessageEventListener() }
                single { ResourcesProviderImpl(get()) as ResourcesProvider }
                single { AppDatabaseImpl(get()) as AppDatabase }
                single { RepositoryImpl(get()) as Repository }
                single { CSVUtils(get(), get()) }
                single { CloseEventListener() }
                single { InputValidator(get()) }
                single { TestsSchedulerProvider() as SchedulerProvider }
            }

            startKoin {
                androidLogger()
                androidContext(this@RoboelectricKoinApplication)
                modules(testAppModule)
            }
        }

        // content resolver initialisation
        if (appContentResolver == null) {
            appContentResolver = shadowOf(this.contentResolver)
        }
    }
}

/**
 * Handy method for assigning an output stream.
 */
fun outputStream(output: (ByteArray) -> Unit): OutputStream {
    return ApplicationOutputStream(output)
}

/**
 * Abstraction for output stream usage.
 */
class ApplicationOutputStream(private val output: (ByteArray) -> Unit) : OutputStream() {

    private val outputBytes = mutableListOf<Byte>()

    override fun write(b: Int) {
        outputBytes.add(b.toByte())
    }

    override fun close() {
        output(outputBytes.toByteArray())
        super.close()
    }
}