package de.ka.jamit.tcalc.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.utils.CSVUtils
import javax.inject.Singleton

/**
 * A SCV utility provider.
 *
 * Created by Thomas Hofmann on 21.07.20
 **/
@Module
@InstallIn(ApplicationComponent::class)
class CSVUtilProvider {

    @Provides
    @Singleton
    fun provideCSVUtil(app: Application, repository: Repository): CSVUtils {
        return CSVUtils(repository, app)
    }
}