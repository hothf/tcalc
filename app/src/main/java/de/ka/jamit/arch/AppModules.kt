package de.ka.jamit.arch

import de.ka.jamit.arch.repo.Repository
import de.ka.jamit.arch.repo.RepositoryImpl
import de.ka.jamit.arch.repo.api.ApiService
import de.ka.jamit.arch.repo.db.AppDatabase
import de.ka.jamit.arch.ui.settings.detail.DetailViewModel
import de.ka.jamit.arch.ui.home.HomeViewModel
import de.ka.jamit.arch.ui.main.MainViewModel
import de.ka.jamit.arch.ui.profile.ProfileViewModel
import de.ka.jamit.arch.ui.profile.dialogs.ProfileDialogViewModel
import de.ka.jamit.arch.ui.settings.SettingsViewModel
import de.ka.jamit.arch.utils.CloseEventListener
import de.ka.jamit.arch.utils.GlobalMessageEventListener
import de.ka.jamit.arch.utils.resources.ResourcesProvider
import de.ka.jamit.arch.utils.resources.ResourcesProviderImpl
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