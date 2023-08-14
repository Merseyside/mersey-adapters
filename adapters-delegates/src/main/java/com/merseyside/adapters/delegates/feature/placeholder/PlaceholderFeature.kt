package com.merseyside.adapters.delegates.feature.placeholder

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.feature.ConfigurableFeature
import com.merseyside.adapters.core.feature.sorting.comparator.HasItemComparator
import com.merseyside.adapters.core.feature.sorting.comparator.ItemComparator
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.comparator.PlaceholderItemComparator
import com.merseyside.adapters.delegates.feature.placeholder.provider.PlaceholderProvider
import com.merseyside.adapters.delegates.feature.placeholder.resolver.EmptyDataResolver
import com.merseyside.adapters.delegates.feature.placeholder.resolver.PlaceholderDataResolver
import com.merseyside.adapters.delegates.feature.placeholder.resolver.state.ResultDataResolver
import com.merseyside.merseyLib.kotlin.entity.result.Result
import kotlinx.coroutines.flow.Flow

class PlaceholderFeature<Parent, Model : VM<Parent>> :
    ConfigurableFeature<Parent, Model, PlaceholderFeature.Config<Parent, Model>>(),
    HasItemComparator<Parent, Model> {

    private lateinit var placeholderDataResolver: PlaceholderDataResolver<Parent, Model>

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
            placeholderDataResolver = config.dataResolver as? PlaceholderDataResolver<Parent, Model>
                ?: throw NullPointerException("Placeholder data resolver not set!")

            placeholderDataResolver.setPlaceholderProvider(
                config.provider
                    ?: throw NullPointerException("Placeholder provider not set!")
            )

            placeholderDataResolver.initAdapter(this)
        }
    }

    /**
     * Calls when Sorting feature is being installed
     */
    override fun getItemComparator(): ItemComparator<out Parent, Parent, out Model> {
        return config.itemComparator
            ?: throw RuntimeException("Item comparator is required.")
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

    open class Config<Parent, Model : VM<Parent>> {
        open var provider: PlaceholderProvider<out Parent, Parent>? = null
        open var dataResolver: PlaceholderDataResolver<Parent, out Model>? = null

        open var itemComparator: ItemComparator<out Parent, Parent, out Model>? =
            null
    }

    companion object {
        const val KEY = "PlaceholderFeature"
    }
}


object Placeholder {

    context(AdapterConfig<Parent, Model>) @Suppress("UNCHECKED_CAST")
    operator fun <Parent, Model : VM<Parent>,
            TConfig : PlaceholderFeature.Config<Parent, Model>> invoke(
        config: TConfig.() -> Unit
    ): PlaceholderFeature<Parent, Model> {
        return PlaceholderFeature<Parent, Model>().also { feature ->
            feature as ConfigurableFeature<Parent, Model, TConfig>
            install(feature, config)
        }
    }

    /**
     * Uses EmptyDataResolver. Shows/hides placeholder when data is empty/not empty.
     * When @param showPlaceholderOnAttach is true placeholder appears when recycler attached and data is empty.
     * If @param showPlaceholderOnAttach is false placeholder shows only if data was set is empty.
     */
    object EmptyData {
        class Config<Item : Parent, Parent>(
            config: Config<Item, Parent>.() -> Unit
        ) {

            var provider: PlaceholderProvider<out Item, Parent>? = null
            var showOnAttach: Boolean = false
            var ignoreClear: Boolean = false

            init {
                apply(config)
            }
        }

        context(AdapterConfig<Parent, Model>)
        operator fun <Item : Parent, Parent, Model : VM<Parent>> invoke(
            config: Config<Item, Parent>.() -> Unit
        ): PlaceholderFeature<Parent, Model> {
            return Placeholder {
                Config(config).let {
                    dataResolver = EmptyDataResolver(it.showOnAttach, it.ignoreClear)
                    provider = it.provider
                }
                itemComparator =
                    PlaceholderItemComparator(
                        provider?.placeholderDelegate?.modelClass ?:
                        throw NullPointerException("Delegate hasn't been provided!"))
            }
        }
    }

    /**
     * Receives and handles Flow<Result> in order to manage placeholder's showing/hiding.
     * Shows placeholder if Result is Success and data is empty.
     */
    object ByResult {
        class Config<Item : Parent, Parent>(config: Config<Item, Parent>.() -> Unit) {
            var provider: PlaceholderProvider<out Item, Parent>? = null

            lateinit var lifecycleOwner: LifecycleOwner
            lateinit var resultFlow: Flow<Result<*>>

            init {
                apply(config)
            }
        }

        context(AdapterConfig<Parent, Model>) @Suppress("UNCHECKED_CAST")
        operator fun <Item : Parent, Parent, Model : VM<Parent>> invoke(
            config: Config<Item, Parent>.() -> Unit
        ): PlaceholderFeature<Parent, Model> {
            return Placeholder {
                Config(config).let {
                    dataResolver = ResultDataResolver(it.lifecycleOwner, it.resultFlow)
                    provider = it.provider
                }
                itemComparator = PlaceholderItemComparator(
                    provider?.placeholderDelegate?.modelClass ?:
                    throw NullPointerException("Delegate hasn't been provided!")
                )
            }
        }
    }
}