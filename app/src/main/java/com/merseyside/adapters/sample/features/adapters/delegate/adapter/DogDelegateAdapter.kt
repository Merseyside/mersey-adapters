package com.merseyside.adapters.sample.features.adapters.delegate.adapter

import com.merseyside.adapters.delegates.delegate.DelegateAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.delegate.entity.Animal
import com.merseyside.adapters.sample.features.adapters.delegate.entity.Dog
import com.merseyside.adapters.sample.features.adapters.delegate.model.DogItemViewModel

class DogDelegateAdapter: DelegateAdapter<Dog, Animal, DogItemViewModel>() {

    override fun createItemViewModel(item: Dog) = DogItemViewModel(item)
    override fun getLayoutIdForItem(viewType: Int) = R.layout.item_dog
    override fun getBindingVariable() = BR.model
}