package com.merseyside.adapters.delegates.feature.placeholder

import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.feature.ConfigurableFeature
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.provider.PlaceholderProvider
import com.merseyside.adapters.delegates.feature.resolver.PlaceholderDataResolver

class PlaceholderFeature<Parent, Model : VM<Parent>> : ConfigurableFeature<Parent, Model, Config<Parent, Model>>() {

    private lateinit var placeholderManager: PlaceholderManager<Parent, Model>

    override fun prepare(configure: Config<Parent, Model>.() -> Unit) {
        config.apply(configure)
    }

    override val config: Config<Parent, Model> = Config()

    override fun install(
        adapterConfig: AdapterConfig<Parent, Model>,
        adapter: IBaseAdapter<Parent, Model>
    ) {
        super.install(adapterConfig, adapter)
        requireCompositeAdapter(adapter) {
            placeholderManager = PlaceholderManager(
                adapterConfig.modelList,
                config.placeholderProvider
                    ?: throw NullPointerException("Placeholder provider not set!"),
                config.placeholderDataResolver
                    ?: throw NullPointerException("Placeholder data resolver not set!")
            )

            placeholderManager.initAdapter(this)
        }
    }

    override val featureKey: String = KEY

    private fun requireCompositeAdapter(
        adapter: IBaseAdapter<Parent, Model>,
        block: CompositeAdapter<Parent, Model>.() -> Unit
    ) {
        if (adapter is CompositeAdapter<Parent, Model>) {
            block(adapter)
        } else throw NotImplementedError("Placeholder feature only supported to use with SimpleCompositeAdapter")
    }

    companion object {
        const val KEY = "PlaceholderFeature"
    }
}

open class Config<Parent, Model : VM<Parent>> {
    var placeholderProvider: PlaceholderProvider<Parent, Model>? = null
    var placeholderDataResolver: PlaceholderDataResolver? = null
}

object Placeholder {
    context(AdapterConfig<Parent, Model>) @Suppress("UNCHECKED_CAST")
    operator fun <Parent, Model : VM<Parent>, TConfig : Config<Parent, Model>> invoke(config: TConfig.() -> Unit): PlaceholderFeature<Parent, Model> {
        return PlaceholderFeature<Parent, Model>().also { feature ->
            feature as ConfigurableFeature<Parent, Model, TConfig>
            install(feature, config)
        }
    }
}