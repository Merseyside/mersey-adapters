package com.merseyside.adapters.sample.features.adapters.delegate.adapter

import com.merseyside.adapters.delegates.SimpleDelegateAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.delegate.entity.ButtonItem
import com.merseyside.adapters.sample.features.adapters.delegate.model.ButtonViewModel

class ButtonDelegateAdapter : SimpleDelegateAdapter<ButtonItem, ButtonViewModel>() {
    override fun createItemViewModel(item: ButtonItem) = ButtonViewModel(item)
    override fun getLayoutIdForItem(viewType: Int) = R.layout.item_button
    override fun getBindingVariable() = BR.model
}