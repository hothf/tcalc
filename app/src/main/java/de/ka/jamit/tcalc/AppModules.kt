package de.ka.jamit.tcalc

import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.RepositoryImpl
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.ui.home.HomeViewModel
import de.ka.jamit.tcalc.ui.home.dialog.HomeEnterDialogViewModel
import de.ka.jamit.tcalc.ui.main.MainViewModel
import de.ka.jamit.tcalc.ui.settings.SettingsViewModel
import de.ka.jamit.tcalc.utils.CloseEventListener
import de.ka.jamit.tcalc.utils.GlobalMessageEventListener
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import de.ka.jamit.tcalc.utils.resources.ResourcesProviderImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Declares all modules used for koin dependency injection.
 */

val appModule = module {

    viewModel { MainViewModel() }
    viewModel { HomeViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { HomeEnterDialogViewModel() }

    single { ResourcesProviderImpl(get()) as ResourcesProvider }
    single { GlobalMessageEventListener() }
    single { CloseEventListener() }

    single { AppDatabase(get()) }
    single { RepositoryImpl(get(), db = get()) as Repository }
}