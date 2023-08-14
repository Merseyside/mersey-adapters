package com.merseyside.adapters.sample.features.adapters.news.model

import com.merseyside.adapters.core.model.AdapterViewModel

class NewsItemViewModel(item: News): AdapterViewModel<News>(item) {

    override val id = item.id

    override fun areItemsTheSame(other: News) = item.id == other.id
}