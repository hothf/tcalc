package de.ka.jamit.tcalc.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import de.ka.jamit.tcalc.utils.resources.ResourcesProviderImpl
import javax.inject.Singleton

/**
 * A resources provider module.
 *
 * Created by Thomas Hofmann on 21.07.20
 **/
@Module
@InstallIn(ApplicationComponent::class)
class ResourcesModule {

    @Provides @Singleton fun provideResourcesProvider(app: Application): ResourcesProvider {
        return ResourcesProviderImpl(app)
    }
}