package com.merseyside.adapters.sample.application

import android.annotation.SuppressLint
import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.application.di.AppModule
import com.merseyside.adapters.sample.application.di.DaggerAppComponent
import com.merseyside.archy.BaseApplication
import com.merseyside.merseyLib.time.Time
import com.merseyside.merseyLib.time.init
import com.merseyside.utils.ThemeManager

class SampleApplication : BaseApplication() {

    lateinit var appComponent : AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = buildComponent()

        Time.init(this)
        ThemeManager.apply(ThemeManager.Theme.DARK)
    }

    private fun buildComponent() =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: SampleApplication

        fun getInstance() : SampleApplication {
            return instance
        }
    }
}