package de.ka.jamit.tcalc

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
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Declares all modules used for koin dependency injection.
 */

val appModule = module {

    viewModel { MainViewModel() }
    viewModel { HomeViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { HomeAddEditDialogViewModel() }
    viewModel { UserDialogViewModel() }
    viewModel { UserAddEditDialogViewModel() }
    viewModel { ImportingDialogViewModel() }
    viewModel { ExportingDialogViewModel() }
    viewModel { CategoryDialogViewModel() }

    single { AppDatabaseImpl(get()) as AppDatabase }
    single { ResourcesProviderImpl(get()) as ResourcesProvider }
    single { GlobalMessageEventListener() }
    single { CloseEventListener() }
    single { CSVUtils(get(), get()) }
    single { InputValidator(get()) }
    single { RepositoryImpl(get()) as Repository }
}