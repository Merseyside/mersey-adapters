package com.merseyside.adapters.sample.application

import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.application.di.AppModule
import com.merseyside.adapters.sample.application.di.DaggerAppComponent
import com.merseyside.archy.BaseApplication
import com.merseyside.utils.ThemeManager

class SampleApplication : BaseApplication() {

    lateinit var appComponent : AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = buildComponent()

        ThemeManager.apply(ThemeManager.Theme.DARK)

        //AdaptersContext.coroutineContext = defaultDispatcher
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