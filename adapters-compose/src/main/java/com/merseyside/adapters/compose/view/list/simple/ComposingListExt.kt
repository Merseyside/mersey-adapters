package com.merseyside.adapters.compose.view.list.simple

import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.model.VM

fun ListConfig.adapterConfig(configure: AdapterConfig<SCV, VM<SCV>>.() -> Unit) {
    adapterConfig = configure
}