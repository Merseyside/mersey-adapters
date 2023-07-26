package com.merseyside.adapters.sample.features.adapters.concat.adapter

import com.merseyside.adapters.SimpleAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.init.initAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.concat.entity.News
import com.merseyside.adapters.sample.features.adapters.concat.model.NewsItemViewModel


class NewsAdapter private constructor(
    override val adapterConfig: AdapterConfig<News, NewsItemViewModel>
) : SimpleAdapter<News, NewsItemViewModel>(adapterConfig) {
    override fun getLayoutIdForViewType(position: Int) = R.layout.item_news
    override fun getBindingVariable() = BR.obj
    override fun createItemViewModel(item: News) = NewsItemViewModel(item)

    companion object {
        operator fun invoke(): NewsAdapter {
            return initAdapter(::NewsAdapter)
        }
    }
}