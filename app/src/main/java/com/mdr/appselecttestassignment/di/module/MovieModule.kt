package com.mdr.appselecttestassignment.di.module

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mdr.appselecttestassignment.data.NetworkStatusImpl
import com.mdr.appselecttestassignment.data.movie.MovieRemoteDataSource
import com.mdr.appselecttestassignment.data.movie.MovieRepoImpl
import com.mdr.appselecttestassignment.data.movie.NYTimesApi
import com.mdr.appselecttestassignment.domain.MovieRepo
import com.mdr.appselecttestassignment.domain.NetworkStatus
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class MovieModule {
    @Named("baseUrl")
    @Provides
    fun baseUrl(): String {
        return "https://api.nytimes.com"
    }

    @Named("apiKey")
    @Provides
    fun apiKey(): String {
        return "OkRN0devgCnIJDqCpwxA2kvr6EGSLYBc"
    }

    @Singleton
    @Provides
    fun api(@Named("baseUrl") baseUrl: String, gson: Gson): NYTimesApi {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
            .create(NYTimesApi::class.java)
    }

    @Provides
    fun gson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Provides
    fun networkStatus(context: Context): NetworkStatus {
        return NetworkStatusImpl(context)
    }

    @Singleton
    @Provides
    fun movieRemoteDataSource(@Named("apiKey") apiKey: String, api: NYTimesApi) =
        MovieRemoteDataSource(api, apiKey)

    @Singleton
    @Provides
    fun movieRepo(
        remote: MovieRemoteDataSource,
        networkStatus: NetworkStatus
    ): MovieRepo = MovieRepoImpl(remote, networkStatus)
}