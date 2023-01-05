package com.merseyside.adapters.sample.features.adapters.concat.di

import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.features.adapters.concat.view.ConcatAdapterFragment
import com.merseyside.archy.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [ConcatModule::class])
interface ConcatComponent {

    fun inject(fragment: ConcatAdapterFragment)
}