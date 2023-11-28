package com.merseyside.adapters.delegates.simple

import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.delegates.simple.DelegateAdapter
import com.merseyside.adapters.delegates.model.SimpleAdapterViewModel
import com.merseyside.adapters.delegates.nestedDelegate.NestedDelegateAdapter

typealias SimpleDelegateAdapter<Item, Model> = DelegateAdapter<Item, Any, Model>
typealias SimpleNestedDelegateAdapter<Item, Model, Data, InnerAdapter> = NestedDelegateAdapter<Item, Any, Model, Data, InnerAdapter>

typealias SimpleAdapterConfig = AdapterConfig<Any, SimpleAdapterViewModel<out Any>>
