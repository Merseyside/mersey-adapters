package com.merseyside.adapters.sample.features.adapters.colors.adapter

import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.merseyside.adapters.delegates.simple.SimpleDelegateAdapter
import com.merseyside.adapters.sample.features.adapters.colors.entity.HexColor
import com.merseyside.adapters.sample.features.adapters.colors.model.ColorItemViewModel
import com.merseyside.utils.convertDpToPixel
import com.merseyside.utils.view.ext.onClick

class ColorsDelegateAdapter : SimpleDelegateAdapter<HexColor, ColorItemViewModel>() {

    init {
        setupViewHolder(createView = { context ->
            TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    convertDpToPixel(context, 20F).toInt()
                )

                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
            }
        }, bind = { view, model ->
            with(view) {
                setBackgroundColor(model.getColor())
                text = model.getColorHex()
                onClick { model.onClick() }
            }
        })
    }

    override fun createItemViewModel(item: HexColor) = ColorItemViewModel(item)

}