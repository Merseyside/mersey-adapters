package com.merseyside.adapters.sample.features.adapters.colors.adapter

import com.merseyside.adapters.core.base.callback.onClick
import com.merseyside.adapters.delegates.simple.SimpleDelegateAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.colors.entity.HexColor
import com.merseyside.adapters.sample.features.adapters.colors.model.ColorItemViewModel

class ColorsDelegateAdapter: SimpleDelegateAdapter<HexColor, ColorItemViewModel>() {

//    init {
//        onClick {
//            removeAsync(it)
//        }
//    }

    override fun getLayoutIdForItem(viewType: Int)  = R.layout.item_color

    override fun getBindingVariable() = BR.viewModel

    override fun createItemViewModel(item: HexColor) = ColorItemViewModel(item)

//    companion object {
//        operator fun invoke(configure: AdapterConfig<HexColor, ColorItemViewModel>.() -> Unit): ColorsAdapter {
//            return initAdapter(::ColorsAdapter, configure)
//        }
//    }
}