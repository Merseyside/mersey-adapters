package com.merseyside.adapters.sample.features.adapters.compose.di

import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.features.adapters.compose.view.ComposeFragment
import com.merseyside.archy.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [ComposeModule::class])
interface ComposeComponent {

    fun inject(fragment: ComposeFragment)
}