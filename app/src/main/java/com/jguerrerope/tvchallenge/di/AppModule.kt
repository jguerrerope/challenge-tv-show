package com.jguerrerope.tvchallenge.di

import android.arch.persistence.room.Room
import com.jguerrerope.tvchallenge.Configuration
import com.jguerrerope.tvchallenge.TvShowApplication
import com.jguerrerope.tvchallenge.api.TMDBService
import com.jguerrerope.tvchallenge.db.TvShowDao
import com.jguerrerope.tvchallenge.db.TvShowDatabase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [(ViewModelModule::class)])
class AppModule {
    @Singleton
    @Provides
    fun provideGithubService(): TMDBService {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        clientBuilder.addInterceptor(loggingInterceptor)

        return Retrofit.Builder()
                .baseUrl(Configuration.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(clientBuilder.build())
                .build()
                .create<TMDBService>(TMDBService::class.java)
    }

    @Singleton
    @Provides
    fun provideTvShowDatabase(app: TvShowApplication): TvShowDatabase =
            Room.databaseBuilder(app, TvShowDatabase::class.java, "tv_show.db").build()

    @Singleton
    @Provides
    fun provideTvShowDao(db: TvShowDatabase): TvShowDao = db.tvShowDao()

}
