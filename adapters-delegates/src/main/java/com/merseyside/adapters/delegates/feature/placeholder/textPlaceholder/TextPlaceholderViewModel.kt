package com.merseyside.adapters.delegates.feature.placeholder.textPlaceholder

import com.merseyside.adapters.delegates.feature.placeholder.viewmodel.PlaceholderViewModel

class TextPlaceholderViewModel(item: TextPlaceholder) : PlaceholderViewModel<TextPlaceholder>(item) {

    fun getText(): String {
        return item.text
    }
}