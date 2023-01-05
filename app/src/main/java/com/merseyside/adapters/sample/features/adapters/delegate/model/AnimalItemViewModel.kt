package com.merseyside.adapters.sample.features.adapters.delegate.model

import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.sample.features.adapters.delegate.entity.Animal

abstract class AnimalItemViewModel<T : Animal>(item: T) : AdapterParentViewModel<T, Animal>(item) {

    fun getName() = item.name
    fun getAge() = item.age
}