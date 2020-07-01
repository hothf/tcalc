package de.ka.jamit.tcalc

import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.RepositoryImpl
import de.ka.jamit.tcalc.repo.api.ApiService
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.ui.settings.detail.DetailViewModel
import de.ka.jamit.tcalc.ui.home.HomeViewModel
import de.ka.jamit.tcalc.ui.main.MainViewModel
import de.ka.jamit.tcalc.ui.profile.ProfileViewModel
import de.ka.jamit.tcalc.ui.profile.dialogs.ProfileDialogViewModel
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
    viewModel { ProfileViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { DetailViewModel() }
    viewModel { ProfileDialogViewModel() }

    single { ResourcesProviderImpl(get()) as ResourcesProvider }
    single { GlobalMessageEventListener() }
    single { CloseEventListener() }

    single { ApiService(get()) }
    single { AppDatabase(get()) }
    single { RepositoryImpl(get(), api = get(), db = get()) as Repository }
}