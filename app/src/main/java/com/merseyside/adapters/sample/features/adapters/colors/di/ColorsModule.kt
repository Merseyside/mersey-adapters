package com.merseyside.adapters.sample.features.adapters.colors.di

import android.app.Application
import com.merseyside.adapters.sample.features.adapters.colors.model.ColorsViewModel
import com.merseyside.adapters.sample.features.adapters.colors.producer.ColorProducer
import com.merseyside.adapters.sample.features.adapters.colors.view.ColorsFragment
import com.merseyside.archy.presentation.di.qualifiers.ApplicationContext
import com.merseyside.archy.presentation.ext.viewModel
import dagger.Module
import dagger.Provides

@Module
class ColorsModule(private val fragment: ColorsFragment) {

    @Provides
    fun provideColorsViewModel(
        @ApplicationContext application: Application,
        colorProducer: ColorProducer
    ) = fragment.viewModel { ColorsViewModel(application, colorProducer) }

    @Provides
    fun provideColorProducer(): ColorProducer {
        return ColorProducer()
    }
}