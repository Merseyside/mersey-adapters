package com.merseyside.adapters.sample.features.adapters.delegate.di

import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.features.adapters.delegate.view.DelegateFragment
import com.merseyside.archy.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [DelegateModule::class])
interface DelegateComponent {

    fun inject(fragment: DelegateFragment)
}