package com.merseyside.adapters.core.feature.dataProvider

import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.utils.InternalAdaptersApi

interface DataObserver<Item, Parent> {

    @Suppress("UNCHECKED_CAST")
    @InternalAdaptersApi
    fun onDataProvidedInternal(adapter: IBaseAdapter<Parent, *>, data: Any) {
        onDataProvided(adapter, data as Item)
    }

    fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: Item)
}