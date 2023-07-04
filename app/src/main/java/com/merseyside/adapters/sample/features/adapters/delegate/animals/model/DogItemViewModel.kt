package com.merseyside.adapters.sample.features.adapters.delegate.animals.model

import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Dog

class DogItemViewModel(obj: Dog): AnimalItemViewModel<Dog>(obj) {

    override fun areContentsTheSame(other: Dog): Boolean {
        return item == other
    }

    override fun areItemsTheSame(other: Dog): Boolean {
        return item.name == other.name
    }
}