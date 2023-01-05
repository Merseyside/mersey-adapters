package com.merseyside.adapters.sample.features.adapters.delegate.adapter

import com.merseyside.adapters.sample.features.adapters.delegate.entity.Animal
import com.merseyside.adapters.sample.features.adapters.delegate.model.AnimalItemViewModel
import com.merseyside.adapters.core.feature.sorting.Comparator

class AnimalsComparator : Comparator<Animal, AnimalItemViewModel<out Animal>>() {
    override fun compare(
        model1: AnimalItemViewModel<out Animal>,
        model2: AnimalItemViewModel<out Animal>
    ): Int {
        return model1.getAge().compareTo(model2.getAge())
    }
}