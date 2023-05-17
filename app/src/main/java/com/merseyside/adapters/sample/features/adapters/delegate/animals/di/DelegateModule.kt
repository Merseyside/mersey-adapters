package com.merseyside.adapters.sample.features.adapters.delegate.animals.di

import android.app.Application
import androidx.fragment.app.Fragment
import com.merseyside.adapters.sample.features.adapters.delegate.animals.model.DelegateViewModel
import com.merseyside.archy.presentation.di.qualifiers.ApplicationContext
import com.merseyside.archy.presentation.ext.viewModel
import dagger.Module
import dagger.Provides

@Module
class DelegateModule(private val fragment: Fragment) {
    @Provides
    fun provideContactsViewModel(
        @ApplicationContext application: Application
    ) = fragment.viewModel { DelegateViewModel(application) }

}