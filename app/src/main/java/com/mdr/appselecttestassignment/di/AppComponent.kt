package com.mdr.appselecttestassignment.di

import android.content.Context
import com.mdr.appselecttestassignment.presentation.main.MainActivity
import com.mdr.appselecttestassignment.di.module.MovieModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MovieModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
        ): AppComponent
    }
}