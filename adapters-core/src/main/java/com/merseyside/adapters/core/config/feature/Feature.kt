package com.merseyside.adapters.core.config.feature

import androidx.annotation.CallSuper
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.model.VM

abstract class Feature<Parent, Model : VM<Parent>> {
    var isInstalled: Boolean = false

    @CallSuper
    open fun install(
        adapterConfig: AdapterConfig<Parent, Model>,
        adapter: IBaseAdapter<Parent, Model>
    ) {
        isInstalled = true
    }

    abstract val featureKey: String
}
