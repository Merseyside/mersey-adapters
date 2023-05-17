package com.merseyside.adapters.sample.features.adapters.delegate.placeholder.adapter

import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.init.initAdapter
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.SimpleCompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.Placeholder

class PlaceholderAdapter(config: AdapterConfig<Any, VM<Any>>) : SimpleCompositeAdapter(config) {

    companion object {
        operator fun invoke(): PlaceholderAdapter {
            return initAdapter(::PlaceholderAdapter) {
                Placeholder {

                }
            }
        }
    }
}