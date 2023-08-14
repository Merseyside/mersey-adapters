package com.merseyside.adapters.delegates.feature.placeholder.textPlaceholder

import com.merseyside.merseyLib.kotlin.contract.Identifiable

data class TextPlaceholder(val text: String): Identifiable<String> {
    override val id: String = "text_placeholder"
}