package com.merseyside.adapters.delegates.feature.resolver

class EmptyDataResolver(private val addOnAttach: Boolean = false) : PlaceholderDataResolver() {

    override fun isShowPlaceholderOnAttach(): Boolean {
        return addOnAttach
    }

    override fun getPlaceholderPosition(): Int {
        return LAST_POSITION
    }

    override fun isPlaceholderVisible(size: Int): Boolean {
        return size == 0
    }
}