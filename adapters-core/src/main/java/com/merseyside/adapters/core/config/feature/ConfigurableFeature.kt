package com.merseyside.adapters.core.config.feature

import com.merseyside.adapters.core.model.VM


abstract class ConfigurableFeature<Parent, Model, Config> : Feature<Parent, Model>()
    where Model: VM<Parent> {

    abstract fun prepare(configure: Config.() -> Unit)

    protected abstract val config: Config

}