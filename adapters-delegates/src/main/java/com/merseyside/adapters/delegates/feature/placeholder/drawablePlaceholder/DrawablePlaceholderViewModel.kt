package com.merseyside.adapters.delegates.feature.placeholder.drawablePlaceholder

import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.merseyside.adapters.delegates.feature.placeholder.viewmodel.PlaceholderViewModel

class DrawablePlaceholderViewModel(
    item: DrawablePlaceholder
) : PlaceholderViewModel<DrawablePlaceholder>(item) {

    @Bindable
    @DrawableRes
    fun getDrawable(): Int {
        return item.drawable
    }
}