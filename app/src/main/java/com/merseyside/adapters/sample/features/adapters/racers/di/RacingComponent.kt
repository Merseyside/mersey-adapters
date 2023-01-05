package com.merseyside.adapters.sample.features.adapters.racers.di

import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.features.adapters.racers.view.RacingFragment
import com.merseyside.archy.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [RacingModule::class])
interface RacingComponent {

    fun inject(fragment: RacingFragment)
}