package de.ka.jamit.tcalc.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.ka.jamit.tcalc.utils.GlobalMessageEventListener
import javax.inject.Singleton

/**
 * A global messenger handler provider.
 *
 * Created by Thomas Hofmann on 21.07.20
 **/
@Module
@InstallIn(ApplicationComponent::class)
class GlobalMessengerModule {

    @Provides @Singleton fun provideGlobalMessenger(): GlobalMessageEventListener {
        return GlobalMessageEventListener()
    }
}