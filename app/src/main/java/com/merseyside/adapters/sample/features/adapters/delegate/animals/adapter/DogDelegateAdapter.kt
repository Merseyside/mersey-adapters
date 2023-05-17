package com.merseyside.adapters.sample.features.adapters.delegate.animals.adapter

import com.merseyside.adapters.delegates.DelegateAdapter
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Animal
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Dog
import com.merseyside.adapters.sample.features.adapters.delegate.animals.model.DogItemViewModel

class DogDelegateAdapter: DelegateAdapter<Dog, Animal, DogItemViewModel>() {

    override fun createItemViewModel(item: Dog) = DogItemViewModel(item)
    override fun getLayoutIdForItem(viewType: Int) = R.layout.item_dog
    override fun getBindingVariable() = BR.model
}