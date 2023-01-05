package com.merseyside.adapters.core.feature.positioning

import com.merseyside.adapters.core.utils.InternalAdaptersApi

interface PositionHandler {

    var position: Int

    @InternalAdaptersApi
    fun onPositionChanged(toPosition: Int) {
        if (position != toPosition) {
            onPositionChanged(fromPosition = position, toPosition = toPosition)
            position = toPosition
        }
    }

    fun onPositionChanged(fromPosition: Int, toPosition: Int)
}