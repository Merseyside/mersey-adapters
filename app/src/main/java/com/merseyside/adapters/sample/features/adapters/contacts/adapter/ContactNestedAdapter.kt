package com.merseyside.adapters.sample.features.adapters.contacts.adapter

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.NestedAdapter
import com.merseyside.adapters.core.async.removeAsync
import com.merseyside.adapters.core.base.callback.onClick
import com.merseyside.adapters.core.config.NestedAdapterConfig
import com.merseyside.adapters.core.config.init.initNestedAdapter
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.viewBinding.asBindingHolder
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.databinding.ItemGroupContactBinding
import com.merseyside.adapters.sample.features.adapters.contacts.entity.ContactGroup
import com.merseyside.adapters.sample.features.adapters.contacts.model.ContactGroupItemViewModel

class ContactNestedAdapter(config: ContactNestedAdapterConfig) : NestedAdapter<ContactGroup, ContactGroupItemViewModel,
        String, ContactAdapter>(config) {

    init {
        onClick { removeAsync(it) }
    }

    override fun getLayoutIdForPosition(position: Int) = R.layout.item_group_contact
    override fun getBindingVariable() = BR.model
    override fun getNestedView(holder: ViewHolder<ContactGroup, ContactGroupItemViewModel>): RecyclerView? {
        return (holder.asBindingHolder().binding as ItemGroupContactBinding).recycler
    }

    override fun createItemViewModel(item: ContactGroup) = ContactGroupItemViewModel(item)
    override fun initNestedAdapter(model: ContactGroupItemViewModel) = ContactAdapter()

    companion object {
        operator fun invoke(
            configure: ContactNestedAdapterConfig.() -> Unit
        ): ContactNestedAdapter {
            return initNestedAdapter(::ContactNestedAdapter, configure)
        }
    }
}

private typealias ContactNestedAdapterConfig = NestedAdapterConfig<ContactGroup, ContactGroupItemViewModel,
        String, ContactAdapter>