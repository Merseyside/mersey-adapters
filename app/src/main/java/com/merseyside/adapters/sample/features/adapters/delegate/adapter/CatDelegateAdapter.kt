package com.merseyside.adapters.sample.features.adapters.delegate.adapter

import com.merseyside.adapters.delegates.delegate.DelegateAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.delegate.entity.Animal
import com.merseyside.adapters.sample.features.adapters.delegate.entity.Cat
import com.merseyside.adapters.sample.features.adapters.delegate.model.CatItemViewModel

class CatDelegateAdapter: DelegateAdapter<Cat, Animal, CatItemViewModel>() {

    override fun createItemViewModel(item: Cat) = CatItemViewModel(item)
    override fun getLayoutIdForItem(viewType: Int) = R.layout.item_cat
    override fun getBindingVariable() = BR.model
}