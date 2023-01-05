package com.merseyside.adapters.sample.features.adapters.compose.di

import android.app.Application
import com.merseyside.adapters.sample.features.adapters.compose.model.ComposeViewModel
import com.merseyside.adapters.sample.features.adapters.compose.view.ComposeFragment
import com.merseyside.archy.presentation.di.qualifiers.ApplicationContext
import com.merseyside.archy.presentation.ext.viewModel
import dagger.Module
import dagger.Provides

@Module
class ComposeModule(private val fragment: ComposeFragment) {

    @Provides
    fun provideComposeViewModel(
        @ApplicationContext application: Application,
    ) = fragment.viewModel { ComposeViewModel(application) }

}