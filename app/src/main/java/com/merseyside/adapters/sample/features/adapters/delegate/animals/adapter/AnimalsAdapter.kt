package com.merseyside.adapters.sample.features.adapters.delegate.animals.adapter

import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.init.initAdapter
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Animal
import com.merseyside.adapters.sample.features.adapters.delegate.animals.model.AnimalItemViewModel

class AnimalsAdapter(
    adapterConfig: AdapterConfig<Animal, AnimalItemViewModel<out Animal>>
) : CompositeAdapter<Animal, AnimalItemViewModel<out Animal>>(adapterConfig) {

    init {
        delegatesManager.addDelegates(
            CatDelegateAdapter(),
            DogDelegateAdapter()
        )
    }

    companion object {
        operator fun invoke(
            configure: AdapterConfig<Animal, AnimalItemViewModel<out Animal>>.() -> Unit
        ): AnimalsAdapter {
            return initAdapter(::AnimalsAdapter, configure)
        }
    }
}