package de.ka.jamit.tcalc.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.ka.jamit.tcalc.utils.schedulers.AndroidSchedulerProvider
import de.ka.jamit.tcalc.utils.schedulers.SchedulerProvider
import javax.inject.Singleton

/**
 * A scheduler provider module.
 *
 * Created by Thomas Hofmann on 21.07.20
 **/
@Module
@InstallIn(ApplicationComponent::class)
class SchedulerModule {

    @Provides @Singleton fun provideScheduler(): SchedulerProvider {
        return AndroidSchedulerProvider()
    }
}