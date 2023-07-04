package com.merseyside.adapters.delegates.feature.placeholder.textPlaceholder

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.DelegateAdapter
import com.merseyside.adapters.delegates.feature.placeholder.provider.SimplePlaceholderProvider

class TextPlaceholderProvider(text: String) : SimplePlaceholderProvider() {

    override val placeholder: Any = TextPlaceholder(text)
    override val placeholderDelegate: DelegateAdapter<out Any, Any, out VM<Any>> = TextPlaceholderDelegate()
}