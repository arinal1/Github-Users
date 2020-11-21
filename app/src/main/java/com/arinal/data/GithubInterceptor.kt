package com.arinal.data

import com.arinal.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class GithubInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(
        chain.request().newBuilder()
            .addHeader("Authorization", BuildConfig.ACCESS_TOKEN)
            .addHeader("Accept", "application/vnd.github.v3+json")
            .build()
    )
}
