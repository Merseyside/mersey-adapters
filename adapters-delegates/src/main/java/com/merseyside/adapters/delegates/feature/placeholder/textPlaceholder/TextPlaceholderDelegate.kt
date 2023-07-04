package com.merseyside.adapters.delegates.feature.placeholder.textPlaceholder

import com.merseyside.adapters.delegates.R
import com.merseyside.adapters.delegates.BR
import com.merseyside.adapters.delegates.simple.SimpleDelegateAdapter

class TextPlaceholderDelegate : SimpleDelegateAdapter<TextPlaceholder, TextPlaceholderViewModel>() {
    override fun getLayoutIdForItem(viewType: Int) = R.layout.view_text_placeholder
    override fun getBindingVariable() = BR.model
    override fun createItemViewModel(item: TextPlaceholder) = TextPlaceholderViewModel(item)
}