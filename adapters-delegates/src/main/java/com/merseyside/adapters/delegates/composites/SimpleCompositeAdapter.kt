package com.merseyside.adapters.delegates.composites

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.NestedAdapterConfig
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.manager.SimpleDelegatesManager

open class SimpleCompositeAdapter(
    adapterConfig: AdapterConfig<Any, VM<Any>>,
    delegatesManager: SimpleDelegatesManager<Any, VM<Any>> = SimpleDelegatesManager()
) : CompositeAdapter<Any, VM<Any>>(adapterConfig, delegatesManager)

abstract class SimpleNestedCompositeAdapter<Data, InnerAdapter>(
    adapterConfig: NestedAdapterConfig<Any, NestedAdapterParentViewModel<out Any, Any, Data>, Data, InnerAdapter>,
    delegatesManager: SimpleDelegatesManager<Any, NestedAdapterParentViewModel<out Any, Any, Data>> = SimpleDelegatesManager()
) : NestedCompositeAdapter<Any, NestedAdapterParentViewModel<out Any, Any, Data>, Data, InnerAdapter>(
    adapterConfig,
    delegatesManager
) where InnerAdapter : BaseAdapter<Data, *>