package com.blanktheevil.inkmangareader

import android.app.Application
import com.blanktheevil.inkmangareader.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class InkApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@InkApplication)
            modules(appModule)
        }
    }
}