package com.merseyside.adapters.delegates.feature.placeholder.drawablePlaceholder

import com.merseyside.adapters.delegates.R
import com.merseyside.adapters.delegates.BR
import com.merseyside.adapters.delegates.simple.SimpleDelegateAdapter

class DrawablePlaceholderDelegate : SimpleDelegateAdapter<DrawablePlaceholder, DrawablePlaceholderViewModel>(){
    override fun getLayoutIdForItem() = R.layout.view_drawable_placeholder
    override fun getBindingVariable() = BR.model
    override fun createItemViewModel(item: DrawablePlaceholder) = DrawablePlaceholderViewModel(item)
}