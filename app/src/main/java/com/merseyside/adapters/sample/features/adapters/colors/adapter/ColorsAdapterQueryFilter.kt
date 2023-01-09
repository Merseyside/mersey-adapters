package com.merseyside.adapters.sample.features.adapters.colors.adapter

import com.merseyside.adapters.core.feature.filtering.AdapterQueryFilter
import com.merseyside.adapters.sample.features.adapters.colors.entity.HexColor
import com.merseyside.adapters.sample.features.adapters.colors.model.ColorItemViewModel

class ColorsAdapterQueryFilter : AdapterQueryFilter<HexColor, ColorItemViewModel>() {
    override fun filter(model: ColorItemViewModel, query: String): Boolean {
        return model.item.getRHexColor().startsWith(query, ignoreCase = true)
    }
}