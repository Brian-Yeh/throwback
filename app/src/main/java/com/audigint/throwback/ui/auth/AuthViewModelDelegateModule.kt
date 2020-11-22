package com.audigint.throwback.ui.auth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class AuthViewModelDelegateModule {
    @Singleton
    @Provides
    fun provideAuthViewModelDelegate(): AuthViewModelDelegate {
        return SpotifyAuthViewModelDelegate()
    }
}