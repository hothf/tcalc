package de.ka.jamit.tcalc.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.ka.jamit.tcalc.utils.CloseEventListener
import javax.inject.Singleton

/**
 * A close event listener provider.
 *
 * Created by Thomas Hofmann on 21.07.20
 **/
@Module
@InstallIn(ApplicationComponent::class)
class CloseEventModule {

    @Provides @Singleton fun provideCloseEventListener(): CloseEventListener {
        return CloseEventListener()
    }
}