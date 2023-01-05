package com.merseyside.adapters.core.feature.filtering

import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.contract.FilterProvider
import com.merseyside.adapters.core.config.feature.ConfigurableFeature
import com.merseyside.adapters.core.model.VM
import java.lang.NullPointerException

open class FilterFeature<Parent, Model> :
    ConfigurableFeature<Parent, Model, Config<Parent, Model>>(), FilterProvider<Parent, Model>
        where Model : VM<Parent> {

    override lateinit var adapterFilter: AdapterFilter<Parent, Model>
    override val config: Config<Parent, Model> = Config()

    override fun prepare(configure: Config<Parent, Model>.() -> Unit) {
        config.apply(configure)
        adapterFilter = config.filter ?: throw NullPointerException("Pass filter instance")
    }

    override fun install(
        adapterConfig: AdapterConfig<Parent, Model>,
        adapter: IBaseAdapter<Parent, Model>
    ) {
        super.install(adapterConfig, adapter)
        adapterFilter.workManager = adapter.workManager
    }

    override val featureKey: String = key

    companion object {
        const val key = "FilterFeature"
    }
}

open class Config<Parent, Model> where Model : VM<Parent> {
    open var filter: AdapterFilter<Parent, Model>? = null
}

object Filtering {
    context (AdapterConfig<Parent, Model>) @Suppress("UNCHECKED_CAST")
    operator fun <Parent,
            Model : VM<Parent>, TConfig : Config<Parent, Model>> invoke(
        config: TConfig.() -> Unit
    ): FilterFeature<Parent, Model> {
        return FilterFeature<Parent, Model>().also { feature ->
            feature as ConfigurableFeature<Parent, Model, TConfig>
            install(feature, config)
        }
    }
}