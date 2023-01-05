package com.merseyside.adapters.sample.features.adapters.contacts.adapter

import com.merseyside.adapters.core.feature.filtering.AdapterFilter
import com.merseyside.adapters.sample.features.adapters.contacts.model.ContactItemViewModel

class ContactsFilter : AdapterFilter<String, ContactItemViewModel>() {

    override fun filter(model: ContactItemViewModel, key: String, filter: Any): Boolean {
        filter as String
        return model.item.contains(filter, ignoreCase = true)
    }
}