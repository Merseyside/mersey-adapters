package com.merseyside.adapters.core.feature.dataProvider

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.base.IBaseAdapter

fun <Data, Parent> DataProvider<Parent>.observe(
    lifecycleOwner: LifecycleOwner,
    onData: (IBaseAdapter<Parent, *>, data: Data) -> Unit
): DataObserver<Data, Parent> {
    val dataObserver = object : DataObserver<Data, Parent> {
        override fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: Data) {
            onData(adapter, data)
        }
    }

    observe(lifecycleOwner, dataObserver)
    return dataObserver
}