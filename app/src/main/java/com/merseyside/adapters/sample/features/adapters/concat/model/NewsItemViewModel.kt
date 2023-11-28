package com.merseyside.adapters.sample.features.adapters.concat.model

import androidx.databinding.Bindable
import com.merseyside.adapters.core.model.AdapterViewModel
import com.merseyside.adapters.sample.features.adapters.concat.entity.News

class NewsItemViewModel(obj: News): AdapterViewModel<News>(obj) {

    @Bindable
    fun getTitle(): String = this.item.title

    @Bindable
    fun getDescription(): String = this.item.description
}