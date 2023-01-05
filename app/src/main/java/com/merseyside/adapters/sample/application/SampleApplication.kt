package com.merseyside.adapters.sample.application

import com.merseyside.archy.BaseApplication
import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.application.di.AppModule
import com.merseyside.adapters.sample.application.di.DaggerAppComponent

class SampleApplication : BaseApplication() {

    lateinit var appComponent : AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = buildComponent()
    }

    private fun buildComponent() =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

    companion object {
        private lateinit var instance: SampleApplication

        fun getInstance() : SampleApplication {
            return instance
        }
    }
}