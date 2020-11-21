package com.arinal

import android.app.Application
import com.arinal.di.appModule
import com.arinal.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class GithubUsersApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@GithubUsersApp)
            androidFileProperties()
            modules(appModule, viewModelModule)
        }
    }

}