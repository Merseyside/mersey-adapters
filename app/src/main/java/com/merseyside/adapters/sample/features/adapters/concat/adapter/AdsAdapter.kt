package com.merseyside.adapters.sample.features.adapters.concat.adapter

import com.merseyside.adapters.SimpleAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.init.initAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.concat.entity.Ads
import com.merseyside.adapters.sample.features.adapters.concat.model.AdsItemViewModel

class AdsAdapter private constructor(
    override val adapterConfig: AdapterConfig<Ads, AdsItemViewModel>
) : SimpleAdapter<Ads, AdsItemViewModel>(adapterConfig) {
    override fun getLayoutIdForPosition(position: Int) = R.layout.item_ads
    override fun getBindingVariable() = BR.obj
    override fun createItemViewModel(item: Ads) = AdsItemViewModel(item)

    companion object {
        operator fun invoke(): AdsAdapter {
            return initAdapter(::AdsAdapter)
        }
    }
}