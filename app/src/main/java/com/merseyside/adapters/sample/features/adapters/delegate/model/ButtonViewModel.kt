package com.merseyside.adapters.sample.features.adapters.delegate.model

import com.merseyside.adapters.delegates.model.SimpleAdapterViewModel
import com.merseyside.adapters.sample.features.adapters.delegate.entity.ButtonItem

class ButtonViewModel(item: ButtonItem) : SimpleAdapterViewModel<ButtonItem>(item) {

    override fun areItemsTheSame(other: ButtonItem) = false
    override fun notifyUpdate() {}

    fun getTitle() = item.title
}