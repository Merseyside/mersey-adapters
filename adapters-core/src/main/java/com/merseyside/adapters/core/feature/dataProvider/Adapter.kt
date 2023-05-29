package com.merseyside.adapters.core.feature.dataProvider

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.base.IBaseAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@JvmName("singleDataProvider")
fun <Parent> IBaseAdapter<Parent, *>.dataProvider(
    viewLifecycleOwner: LifecycleOwner,
    providerFlow: Flow<*>,
    dataObserver: DataObserver<*, Parent>,
    observeWhenAttached: Boolean = false
): DataProvider<Parent> {
    return DataProvider(
        this,
        viewLifecycleOwner,
        providerFlow,
        observeWhenAttached
    ).apply { addObserver(dataObserver) }
}

fun <Data, Parent> IBaseAdapter<Parent, *>.dataProvider(
    viewLifecycleOwner: LifecycleOwner,
    providerListFlow: Flow<Data>,
    dataObserver: DataObserver<*, Parent>,
    observeWhenAttached: Boolean = false,
    mapper: (Data) -> Parent
): DataProvider<Parent> {
    return DataProvider(
        this,
        viewLifecycleOwner,
        providerListFlow.map(mapper),
        observeWhenAttached
    ).apply { addObserver(dataObserver) }
}