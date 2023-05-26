package com.merseyside.adapters.core.feature.dataProvider

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.base.IBaseAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//fun <Parent> IBaseAdapter<Parent, *>.dataProvider(
//    viewLifecycleOwner: LifecycleOwner,
//    providerListFlow: Flow<List<*>>,
//    dataObserver: DataObserver<*, Parent>,
//    observeWhenAttached: Boolean = false
//): DataProvider<Parent> {
//    return DataProvider(
//        this,
//        viewLifecycleOwner,
//        providerListFlow,
//        observeWhenAttached
//    ).apply { addObserver(dataObserver) }
//}

//fun < Parent> IBaseAdapter<Parent, *>.dataProvider(
//    viewLifecycleOwner: LifecycleOwner,
//    providerListFlow: Flow<List<Parent>>,
//    observeWhenAttached: Boolean = false,
//    onDataProvided: (adapter: IBaseAdapter<Parent, *>, data: List<Parent>) -> Unit
//): DataProvider<Parent> {
//
//    val dataObserver = object : DataObserver<out Parent, Parent>() {
//        override fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: List<Parent>) {
//            onDataProvided(adapter, data)
//        }
//
//        override fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: List<Nothing>) {
//            TODO("Not yet implemented")
//        }
//    }
//
//    return dataProvider(
//        viewLifecycleOwner,
//        providerListFlow,
//        dataObserver,
//        observeWhenAttached
//    )
//}

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

//fun <Data,  Parent> IBaseAdapter<Parent, *>.dataProvider(
//    viewLifecycleOwner: LifecycleOwner,
//    providerListFlow: Flow<List<Data>>,
//    observeWhenAttached: Boolean = false,
//    mapper: (Data) -> Parent,
//    onDataProvided: (adapter: IBaseAdapter<Parent, *>, data: List<Parent>) -> Unit
//): DataProvider<Parent> {
//
//    val dataObserver = object : DataObserver<Parent>() {
//        override fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: List<Parent>) {
//            onDataProvided(adapter, data)
//        }
//    }
//
//    return dataProvider(
//        viewLifecycleOwner,
//        providerListFlow,
//        dataObserver,
//        observeWhenAttached,
//        mapper
//    )
//}

//@JvmName("singleDataProvider")
//fun <Data,  Parent> IBaseAdapter<Parent, *>.dataProvider(
//    viewLifecycleOwner: LifecycleOwner,
//    providerFlow: Flow<Data>,
//    dataObserver: DataObserver<*, Parent>,
//    observeWhenAttached: Boolean = false,
//    mapper: (Data) -> Parent
//): DataProvider<Parent> {
//    return dataProvider(
//        viewLifecycleOwner,
//        providerFlow.mapToList(),
//        dataObserver,
//        observeWhenAttached,
//        mapper
//    )
//}