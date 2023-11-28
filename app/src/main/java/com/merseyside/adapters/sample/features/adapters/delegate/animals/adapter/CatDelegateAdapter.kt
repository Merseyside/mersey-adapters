package com.merseyside.adapters.sample.features.adapters.delegate.animals.adapter

import com.merseyside.adapters.delegates.simple.DelegateAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Animal
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Cat
import com.merseyside.adapters.sample.features.adapters.delegate.animals.model.CatItemViewModel

class CatDelegateAdapter: DelegateAdapter<Cat, Animal, CatItemViewModel>() {

    override fun createItemViewModel(item: Cat) = CatItemViewModel(item)
    override fun getLayoutIdForItem() = R.layout.item_cat
    override fun getBindingVariable() = BR.model
}