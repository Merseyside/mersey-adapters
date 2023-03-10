package com.merseyside.adapters.sample.features.adapters.delegate.model

import com.merseyside.adapters.sample.features.adapters.delegate.entity.Cat

class CatItemViewModel(item: Cat): AnimalItemViewModel<Cat>(item) {

    override fun notifyUpdate() {}

    override fun areContentsTheSame(other: Cat): Boolean {
        return this.item == other
    }
}