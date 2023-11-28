package com.merseyside.adapters.sample.features.adapters.delegate.animals.model

import com.merseyside.adapters.delegates.model.SimpleAdapterViewModel
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.ButtonItem

class ButtonViewModel(item: ButtonItem) : SimpleAdapterViewModel<ButtonItem>(item) {

    override fun areItemsTheSame(other: ButtonItem) = false

    fun getTitle() = item.title
}