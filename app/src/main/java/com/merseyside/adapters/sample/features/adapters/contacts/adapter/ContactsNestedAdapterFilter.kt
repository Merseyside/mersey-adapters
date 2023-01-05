package com.merseyside.adapters.sample.features.adapters.contacts.adapter

import com.merseyside.adapters.core.feature.filtering.NestedAdapterFilter
import com.merseyside.adapters.sample.features.adapters.contacts.entity.ContactGroup
import com.merseyside.adapters.sample.features.adapters.contacts.model.ContactGroupItemViewModel

class ContactsNestedAdapterFilter : NestedAdapterFilter<ContactGroup, ContactGroupItemViewModel>() {

    override fun filter(model: ContactGroupItemViewModel, key: String, filter: Any): Boolean {
        return true
    }

    override fun filter(model: ContactGroupItemViewModel, hasItems: Boolean): Boolean {
        return hasItems
    }

    companion object {
        const val QUERY_KEY = "query"
    }
}