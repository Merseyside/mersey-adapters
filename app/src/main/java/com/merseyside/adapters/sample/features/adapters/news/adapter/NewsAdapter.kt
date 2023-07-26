package com.merseyside.adapters.sample.features.adapters.news.adapter

import com.merseyside.adapters.SimpleAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.init.initAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.news.model.News
import com.merseyside.adapters.sample.features.adapters.news.model.NewsItemViewModel

class NewsAdapter private constructor(config: AdapterConfig<News, NewsItemViewModel>): SimpleAdapter<News, NewsItemViewModel>(config) {

    override fun getLayoutIdForViewType(position: Int) = R.layout.item_news1

    override fun getBindingVariable() = BR.model

    override fun createItemViewModel(item: News) = NewsItemViewModel(item)

    companion object {
        operator fun invoke(): NewsAdapter {
            return initAdapter(::NewsAdapter)
        }
    }

}