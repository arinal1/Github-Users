package com.arinal.di

import com.arinal.BuildConfig
import com.arinal.data.GithubApi
import com.arinal.data.GithubInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    factory { GithubInterceptor() }
    factory { provideHttpLogging() }
    single { provideOkHttpClientBuilder(get(), get()) }
    single { provideGithubApi(get()) }
}

fun provideHttpLogging() = HttpLoggingInterceptor().apply {
    apply { level = HttpLoggingInterceptor.Level.BODY }
}

fun provideOkHttpClientBuilder(
    githubInterceptor: GithubInterceptor,
    loggingInterceptor: HttpLoggingInterceptor
) = OkHttpClient().newBuilder()
    .addInterceptor(loggingInterceptor)
    .addInterceptor(githubInterceptor)
    .build()

fun provideGithubApi(okHttp: OkHttpClient): GithubApi = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttp)
    .baseUrl(BuildConfig.BASE_URL)
    .build()
    .create(GithubApi::class.java)
