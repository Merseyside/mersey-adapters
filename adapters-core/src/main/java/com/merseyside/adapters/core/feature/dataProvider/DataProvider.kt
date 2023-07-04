package com.merseyside.adapters.core.feature.dataProvider

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import kotlinx.coroutines.flow.Flow
import java.lang.NullPointerException

open class DataProvider<Parent>(
    private val adapter: IBaseAdapter<Parent, *>,
    private val viewLifecycleOwner: LifecycleOwner,
    private val providerFlow: Flow<*>,
    observeWhenAttached: Boolean = false
) {
    private val dataObservers: MutableList<DataObserver<*, Parent>> = mutableListOf()

    private var liveData: LiveData<*>? = null

    private val onAttachListener: OnAttachToRecyclerViewListener by lazy {
        object : OnAttachToRecyclerViewListener {
            override fun onAttached(recyclerView: RecyclerView, adapter: IBaseAdapter<*, *>) {
                startObserving()
            }

            override fun onDetached(recyclerView: RecyclerView, adapter: IBaseAdapter<*, *>) {
                stopObserving()
            }
        }
    }

    init {
        if (observeWhenAttached) {
            adapter.addOnAttachToRecyclerViewListener(onAttachListener)
        }
    }

    fun addObserver(observer: DataObserver<*, Parent>) {
        dataObservers.add(observer)
        startObserving()
    }

    private fun startObserving() {
        if (liveData != null) return
        liveData = providerFlow.asLiveData().apply {
            observe(viewLifecycleOwner) {
                notifyObservers(it ?: throw NullPointerException())
            }
        }
    }

    @OptIn(InternalAdaptersApi::class)
    private fun notifyObservers(data: Any) {
        dataObservers.forEach { it.onDataProvidedInternal(adapter, data) }
    }

    private fun stopObserving() {
        liveData?.removeObservers(viewLifecycleOwner)
        liveData = null
    }
}



