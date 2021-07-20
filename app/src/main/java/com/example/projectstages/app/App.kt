package com.example.projectstages.app

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    private lateinit var componentOld: AppComponentOld

    override fun onCreate() {
        super.onCreate()
        componentOld = AppComponentOld(this)
    }

    companion object {
        val Context.appComponentOld: AppComponentOld
            get() = (this.applicationContext as App).componentOld
    }
}