package com.merseyside.adapters.sample.features.adapters.contacts.di

import com.merseyside.adapters.sample.application.di.AppComponent
import com.merseyside.adapters.sample.features.adapters.contacts.view.ContactFragment
import com.merseyside.archy.presentation.di.qualifiers.FragmentScope
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [ContactsModule::class])
interface ContactsComponent {

    fun inject(fragment: ContactFragment)
}