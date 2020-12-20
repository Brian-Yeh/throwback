package com.audigint.throwback.di

import android.content.Context
import android.content.SharedPreferences
import com.audigint.throwback.data.SongRoomDatabase
import com.audigint.throwback.utill.Constants
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

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }
}