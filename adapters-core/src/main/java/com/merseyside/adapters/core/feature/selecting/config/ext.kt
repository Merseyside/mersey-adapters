package com.merseyside.adapters.core.feature.selecting.config

import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.feature.selecting.AdapterSelect
import com.merseyside.adapters.core.feature.selecting.SelectProvider
import com.merseyside.adapters.core.model.VM

fun <Parent, Model : VM<Parent>> AdapterConfig<Parent, Model>.getAdapterSelect(): AdapterSelect<Parent, Model>? {
    return featureList.filterIsInstance<SelectProvider<Parent, Model>>()
        .firstOrNull()?.adapterSelect
}