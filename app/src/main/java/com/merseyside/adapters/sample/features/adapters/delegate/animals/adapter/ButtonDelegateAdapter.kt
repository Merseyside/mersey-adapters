package com.merseyside.adapters.sample.features.adapters.delegate.animals.adapter

import com.merseyside.adapters.delegates.simple.SimpleDelegateAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.ButtonItem
import com.merseyside.adapters.sample.features.adapters.delegate.animals.model.ButtonViewModel

class ButtonDelegateAdapter : SimpleDelegateAdapter<ButtonItem, ButtonViewModel>() {
    override fun createItemViewModel(item: ButtonItem) = ButtonViewModel(item)
    override fun getLayoutIdForItem() = R.layout.item_button
    override fun getBindingVariable() = BR.model
}