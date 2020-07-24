package de.ka.jamit.tcalc.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.AppDatabaseDao
import javax.inject.Singleton

/**
 * A database module offers a database service.
 *
 * Created by Thomas Hofmann on 21.07.20
 **/
@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {

    @Provides @Singleton fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides @Singleton fun provideDatabaseDao(appDatabase: AppDatabase): AppDatabaseDao {
        return appDatabase.databaseDao()
    }
}