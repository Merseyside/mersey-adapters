package com.merseyside.adapters.sample.features.adapters.colors.model

import com.merseyside.adapters.core.model.AdapterViewModel
import com.merseyside.adapters.sample.features.adapters.colors.entity.HexColor

class ColorItemViewModel(obj: HexColor) : AdapterViewModel<HexColor>(obj) {

    fun getColor(): Int {
        return item.color
    }

    fun getColorHex(): String {
        return item.getHex()
    }

    override fun toString(): String {
        return getColorHex()
    }

}