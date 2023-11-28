package com.merseyside.adapters.delegates.feature.placeholder.textPlaceholder


import com.merseyside.adapters.delegates.feature.placeholder.provider.SimplePlaceholderProvider

class TextPlaceholderProvider(text: String) : SimplePlaceholderProvider<TextPlaceholder>() {

    override val placeholder: TextPlaceholder = TextPlaceholder(text)
    override val placeholderDelegate = TextPlaceholderDelegate()
}