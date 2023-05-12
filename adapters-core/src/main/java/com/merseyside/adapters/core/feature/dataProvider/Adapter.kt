package com.merseyside.adapters.core.feature.dataProvider

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.merseyLib.kotlin.coroutines.flow.ext.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <Parent> IBaseAdapter<Parent, *>.dataProvider(
    viewLifecycleOwner: LifecycleOwner,
    providerListFlow: Flow<List<Parent>>,
    applyData: IBaseAdapter<Parent, *>.(data: List<Parent>) -> Unit
): DataProvider<Parent> {
    return object : DataProvider<Parent>(this, viewLifecycleOwner, providerListFlow) {
        override fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: List<Parent>) {
            applyData(adapter, data)
        }
    }
}

@JvmName("singleDataProvider")
fun <Parent> IBaseAdapter<Parent, *>.dataProvider(
    viewLifecycleOwner: LifecycleOwner,
    providerFlow: Flow<Parent>,
    applyData: IBaseAdapter<Parent, *>.(data: List<Parent>) -> Unit
): DataProvider<Parent> {
    return dataProvider(viewLifecycleOwner, providerFlow.mapToList(), applyData)
}

fun <Data, Parent> IBaseAdapter<Parent, *>.dataProvider(
    viewLifecycleOwner: LifecycleOwner,
    providerListFlow: Flow<List<Data>>,
    mapper: (Data) -> Parent,
    applyData: IBaseAdapter<Parent, *>.(data: List<Parent>) -> Unit
): DataProvider<Parent> {
    return object : DataProvider<Parent>(this, viewLifecycleOwner, providerListFlow.map { it.map(mapper) }) {
        override fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: List<Parent>) {
            applyData(adapter, data)
        }
    }
}

@JvmName("singleDataProvider")
fun <Data, Parent> IBaseAdapter<Parent, *>.dataProvider(
    viewLifecycleOwner: LifecycleOwner,
    providerFlow: Flow<Data>,
    mapper: (Data) -> Parent,
    applyData: IBaseAdapter<Parent, *>.(data: List<Parent>) -> Unit
): DataProvider<Parent> {
    return dataProvider(viewLifecycleOwner, providerFlow.mapToList(), mapper, applyData)
}