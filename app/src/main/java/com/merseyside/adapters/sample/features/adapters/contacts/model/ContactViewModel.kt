package com.merseyside.adapters.sample.features.adapters.contacts.model

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.merseyside.adapters.sample.features.adapters.contacts.entity.ContactGroup
import com.merseyside.adapters.sample.features.adapters.contacts.producer.ContactProducer
import com.merseyside.archy.presentation.model.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContactViewModel(
    application: Application,
    private val contactProducer: ContactProducer
) : AndroidViewModel(application) {

    val contactsFlow: Flow<List<ContactGroup>> = contactProducer.getContactsSharedFlow()

    fun populate() {
        viewModelScope.launch {
            contactProducer.generateRandomContacts()
        }
    }
}