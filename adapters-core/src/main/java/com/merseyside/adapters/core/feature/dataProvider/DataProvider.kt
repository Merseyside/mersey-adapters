package com.merseyside.adapters.core.feature.dataProvider

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import com.merseyside.adapters.core.base.IBaseAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class DataProvider<Parent>(
    adapter: IBaseAdapter<Parent, *>,
    private val viewLifecycleOwner: LifecycleOwner,
    private val providerFlow: Flow<List<Parent>>
) {

    init {
        providerFlow.asLiveData().observe(viewLifecycleOwner) {
            onDataProvided(adapter, it)
        }
    }

    abstract fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: List<Parent>)
}



