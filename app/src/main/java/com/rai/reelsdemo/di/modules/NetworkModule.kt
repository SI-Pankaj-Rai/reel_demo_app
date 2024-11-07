package com.rai.reelsdemo.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rai.reelsdemo.data.remote.HomeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    internal fun providesGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideOkHttp():OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(15,TimeUnit.SECONDS)
            .readTimeout(15,TimeUnit.SECONDS)
            .writeTimeout(15,TimeUnit.SECONDS)

    }


    @Singleton
    @Provides
    fun providesRetrofit(
        gson: Gson,
        httpClient: OkHttpClient.Builder
    ):Retrofit{
        return Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
    }

    @Singleton
    @Provides
    fun provideHomeApiService(retrofit: Retrofit):HomeApiService{
        return retrofit.create(HomeApiService::class.java)
    }
}