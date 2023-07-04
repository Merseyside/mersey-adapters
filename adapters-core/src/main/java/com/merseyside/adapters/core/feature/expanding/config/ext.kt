package com.merseyside.adapters.core.feature.expanding.config

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.NestedAdapterConfig
import com.merseyside.adapters.core.feature.expanding.AdapterExpand
import com.merseyside.adapters.core.feature.expanding.ExpandFeature
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM

fun <Parent, Model : NestedAdapterParentViewModel<out Parent, Parent, Data>,
        Data, InnerAdapter : BaseAdapter<Data, out VM<Data>>>
        NestedAdapterConfig<Parent, Model, Data, InnerAdapter>.getAdapterExpand(): AdapterExpand<Parent, Model>? {
    return featureList.filterIsInstance<ExpandFeature<Parent, Model, Data, InnerAdapter>>()
        .firstOrNull()?.adapterExpand
}