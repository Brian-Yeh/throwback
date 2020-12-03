package com.audigint.throwback.di

import android.content.Context
import com.audigint.throwback.data.SongRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideSongRoomDatabase(@ApplicationContext context: Context): SongRoomDatabase {
        return SongRoomDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideSongDao(database: SongRoomDatabase) = database.songDao()
}