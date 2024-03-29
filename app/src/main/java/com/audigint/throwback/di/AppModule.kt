package com.audigint.throwback.di

import android.content.Context
import com.audigint.throwback.data.*
import com.audigint.throwback.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Singleton
    @Provides
    fun provideSongRepository(
        songDao: SongDao,
        sharedPreferencesStorage: SharedPreferencesStorage
    ): SongRepository = DefaultSongRepository(songDao, sharedPreferencesStorage)

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): PreferencesStorage =
        SharedPreferencesStorage(context)

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor() =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Singleton
    @Provides
    fun provideSpotifyServiceInterceptor(sharedPreferences: PreferencesStorage) =
        SpotifyServiceInterceptor(sharedPreferences)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        spotifyServiceInterceptor: SpotifyServiceInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(spotifyServiceInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.SPOTIFY_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun provideSpotifyWebApiService(retrofit: Retrofit): SpotifyWebApiService =
        retrofit.create(SpotifyWebApiService::class.java)

    @Singleton
    @Provides
    fun provideSpotifyWebApiHelper(apiHelper: SpotifyWebApiHelperImpl): SpotifyWebApiHelper =
        apiHelper

    @Singleton
    @Provides
    fun provideMetadataService(spotifyWebApiHelper: SpotifyWebApiHelper): MetadataService =
        SpotifyMetadataService(spotifyWebApiHelper)
}