package de.ka.jamit.tcalc.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.RepositoryImpl
import de.ka.jamit.tcalc.repo.db.AppDatabaseDao
import javax.inject.Singleton

/**
 * Provides the repository.
 *
 * Created by Thomas Hofmann on 21.07.20
 **/
@Module
@InstallIn(ApplicationComponent::class)
class RepositoryModule {

    @Provides @Singleton fun provideRepository(database: AppDatabaseDao): Repository {
        return RepositoryImpl(database)
    }
}
