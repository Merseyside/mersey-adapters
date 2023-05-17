package com.merseyside.adapters.delegates.feature.resolver

abstract class PlaceholderDataResolver {

    abstract fun getPlaceholderPosition(): Int
    abstract fun isPlaceholderVisible(size: Int): Boolean

    /**
     * @return true if we want to add placeholder when attached to recycler view.
     * @return false by default
     */
    open fun isShowPlaceholderOnAttach(): Boolean {
        return false
    }

    companion object {
        const val LAST_POSITION = -1
    }
}