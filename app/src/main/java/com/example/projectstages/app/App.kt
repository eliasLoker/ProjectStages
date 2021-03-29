package com.example.projectstages.app

import android.app.Application
import android.content.Context

class App : Application() {

    private lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = AppComponent(this)
    }

    companion object {
        val Context.appComponent: AppComponent
            get() = (this.applicationContext as App).component
    }
}