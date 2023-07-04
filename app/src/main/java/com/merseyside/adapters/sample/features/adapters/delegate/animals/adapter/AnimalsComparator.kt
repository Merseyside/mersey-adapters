package com.merseyside.adapters.sample.features.adapters.delegate.animals.adapter

import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Animal
import com.merseyside.adapters.sample.features.adapters.delegate.animals.model.AnimalItemViewModel
import com.merseyside.adapters.core.feature.sorting.comparator.Comparator

class AnimalsComparator : Comparator<Animal, AnimalItemViewModel<out Animal>>() {
    override fun compare(
        model1: AnimalItemViewModel<out Animal>,
        model2: AnimalItemViewModel<out Animal>
    ): Int {
        return model1.getAge().compareTo(model2.getAge())
    }
}