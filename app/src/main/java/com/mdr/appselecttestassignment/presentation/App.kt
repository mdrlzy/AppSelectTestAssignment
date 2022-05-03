package com.mdr.appselecttestassignment.presentation

import android.app.Application
import com.mdr.appselecttestassignment.di.AppComponent
import com.mdr.appselecttestassignment.di.DaggerAppComponent

class App : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        instance = this
        appComponent = DaggerAppComponent.factory().create(this)
    }

    companion object {
        lateinit var instance: App
    }
}