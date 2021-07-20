package com.example.projectstages.app.inject

import android.app.Application
import com.example.projectstages.app.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

@AppScope
@Component(modules = [AppModule::class, AndroidInjectionModule::class, FragmentBuilder::class])
interface AppComponent {

    fun inject(instance: App)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application) : Builder

        fun build() : AppComponent
    }
}