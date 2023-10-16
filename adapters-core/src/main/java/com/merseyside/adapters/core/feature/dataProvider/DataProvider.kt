package com.merseyside.adapters.core.feature.dataProvider

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import kotlinx.coroutines.flow.Flow

open class DataProvider<Parent>(
    private val adapter: IBaseAdapter<Parent, *>,
    providerFlow: Flow<*>,
    private val observeWhenAttached: Boolean = false
) {

    private var liveData: LiveData<*> = providerFlow.asLiveData()

    fun observe(lifecycleOwner: LifecycleOwner, observer: DataObserver<*, Parent>) {
        liveData.observe(lifecycleOwner) { data ->
            if (!observeWhenAttached || adapter.isAttached) {
                @OptIn(InternalAdaptersApi::class)
                observer.onDataProvidedInternal(adapter, data)
            }
        }
    }

    fun observeForever(observer: DataObserver<*, Parent>) {
        liveData.observeForever { data ->
            if (!observeWhenAttached || adapter.isAttached) {
                @OptIn(InternalAdaptersApi::class)
                observer.onDataProvidedInternal(adapter, data)
            }
        }
    }
}



