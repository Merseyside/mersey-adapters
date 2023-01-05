package com.merseyside.adapters.sample.application.main.fragment.di

import com.merseyside.archy.presentation.di.qualifiers.FragmentScope
import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.application.main.fragment.view.MainFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [MainModule::class])
interface MainComponent {

    fun inject(fragment: MainFragment)
}