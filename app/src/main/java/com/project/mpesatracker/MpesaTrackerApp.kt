package com.project.mpesatracker

import android.annotation.SuppressLint
import android.app.Application
import com.project.mpesatracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MpesaTrackerApp : Application() {
    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MpesaTrackerApp)
            modules(appModule)
        }
    }
} 