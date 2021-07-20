package com.example.projectstages.app

import android.app.Application
import android.content.Context
import com.example.projectstages.app.inject.AppComponent
import com.example.projectstages.app.inject.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)
        appComponent.inject(this)
    }

    fun initDagger(app: App) : AppComponent =
        DaggerAppComponent
            .builder()
            .application(app)
            .build()

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}