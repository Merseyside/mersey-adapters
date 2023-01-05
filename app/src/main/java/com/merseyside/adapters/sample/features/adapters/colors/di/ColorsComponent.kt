package com.merseyside.adapters.sample.features.adapters.colors.di

import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.features.adapters.colors.view.ColorsFragment
import com.merseyside.archy.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [ColorsModule::class])
interface ColorsComponent {

    fun inject(fragment: ColorsFragment)
}