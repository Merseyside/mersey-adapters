package com.merseyside.adapters.sample.features.adapters.colors.adapter

import com.merseyside.adapters.core.feature.filtering.SimpleAdapterFilter
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.sample.features.adapters.colors.model.ColorItemViewModel

class ColorsFilter : SimpleAdapterFilter() {
    override fun filter(model: VM<Any>, key: String, filter: Any): Boolean {
        return if (model !is ColorItemViewModel) true
        else {
            val query = filter as String
            when (key) {
                R_COLOR_FILTER -> model.item.getRHexColor().startsWith(query, ignoreCase = true)
                G_COLOR_FILTER -> model.item.getGHexColor().startsWith(query, ignoreCase = true)
                B_COLOR_FILTER -> model.item.getBHexColor().startsWith(query, ignoreCase = true)
                else -> false
            }
        }
    }

    companion object {
        const val R_COLOR_FILTER = "rcolor"
        const val G_COLOR_FILTER = "gcolor"
        const val B_COLOR_FILTER = "bcolor"
    }
}

//class ColorsFilter : AdapterFilter<HexColor, ColorItemViewModel>() {
//
//    override fun filter(model: ColorItemViewModel, key: String, filter: Any): Boolean {
//        val query = filter as String
//        return when (key) {
//            R_COLOR_FILTER -> model.item.getRHexColor().startsWith(query, ignoreCase = true)
//            G_COLOR_FILTER -> model.item.getGHexColor().startsWith(query, ignoreCase = true)
//            B_COLOR_FILTER -> model.item.getBHexColor().startsWith(query, ignoreCase = true)
//            else -> false
//        }
//    }
//
//    companion object {
//        const val R_COLOR_FILTER = "rcolor"
//        const val G_COLOR_FILTER = "gcolor"
//        const val B_COLOR_FILTER = "bcolor"
//    }
//}