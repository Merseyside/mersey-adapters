package com.merseyside.adapters.core.config

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import kotlinx.coroutines.CoroutineScope

fun <Parent, Model : VM<Parent>> config(
    scope: CoroutineScope
): AdapterConfig<Parent, Model> {
    return AdapterConfig { coroutineScope = scope }
}

@Suppress("UNCHECKED_CAST")
fun <R : AdapterConfig<Parent, Model>, Parent, Model : VM<Parent>> config(
    init: AdapterConfig<Parent, Model>.() -> Unit
): R {
    return AdapterConfig(init) as R
}

@Suppress("UNCHECKED_CAST")
fun <R : NestedAdapterConfig<Parent, Model, InnerData, InnerAdapter>, Parent,
        Model : NestedAdapterParentViewModel<out Parent, Parent, InnerData>,
        InnerData, InnerAdapter : BaseAdapter<InnerData, out VM<InnerData>>> config(
    init: NestedAdapterConfig<Parent, Model, InnerData, InnerAdapter>.() -> Unit
): R {
    val config = NestedAdapterConfig<Parent, Model, InnerData, InnerAdapter>()
    config.apply(init)

    return config as R
}