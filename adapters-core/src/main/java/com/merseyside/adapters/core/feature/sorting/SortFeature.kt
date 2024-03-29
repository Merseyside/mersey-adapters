package com.merseyside.adapters.core.feature.sorting

import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.contract.ModelListProvider
import com.merseyside.adapters.core.config.contract.UpdateLogicProvider
import com.merseyside.adapters.core.config.feature.ConfigurableFeature
import com.merseyside.adapters.core.config.update.UpdateActions
import com.merseyside.adapters.core.config.update.UpdateLogic
import com.merseyside.adapters.core.config.update.sorted.SortedUpdate
import com.merseyside.adapters.core.feature.sorting.comparator.Comparator
import com.merseyside.adapters.core.feature.sorting.comparator.ComparatorManager
import com.merseyside.adapters.core.feature.sorting.comparator.ComparatorProvider
import com.merseyside.adapters.core.feature.sorting.comparator.ItemComparator
import com.merseyside.adapters.core.feature.sorting.config.getItemComparators
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.SortedModelList
import com.merseyside.adapters.core.feature.sorting.sortedList.SortedList
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi

open class SortFeature<Parent, Model> : ComparatorProvider<Parent, Model>,
    ConfigurableFeature<Parent, Model, SortFeature.Config<Parent, Model>>(),
    ModelListProvider<Parent, Model>, UpdateLogicProvider<Parent, Model>
        where Model : VM<Parent> {

    override val config: Config<Parent, Model> = Config()
    override lateinit var modelList: SortedModelList<Parent, Model>

    override lateinit var comparator: Comparator<Parent, Model>

    override fun prepare(configure: Config<Parent, Model>.() -> Unit) {
        config.apply(configure)
    }

    @Suppress("UNCHECKED_CAST")
    override fun install(
        adapterConfig: AdapterConfig<Parent, Model>,
        adapter: IBaseAdapter<Parent, Model>
    ) {
        super.install(adapterConfig, adapter)
        val itemComparators = adapterConfig.getItemComparators()
        itemComparators.forEach { config.itemComparators.add(it) }

        comparator = resolveComparator(adapterConfig, config)
        val modelClass: Class<Model> = try {
            comparator.getModelClass() as Class<Model>
        } catch (e: IllegalStateException) {
            getModelClass(adapter)
        }

        val sortedList = SortedList(modelClass)
        modelList = SortedModelList(adapterConfig.workManager, sortedList, comparator)
    }

    private fun resolveComparator(
        adapterConfig: AdapterConfig<Parent, Model>,
        config: Config<Parent, Model>
    ): Comparator<Parent, Model> {
        config.comparator.apply {
            workManager = adapterConfig.workManager
            setOnComparatorUpdateCallback {
                modelList.recalculateItemPositions()
            }
        }

        return if (config.itemComparators.isEmpty()) config.comparator
        else ComparatorManager(config.comparator, config.itemComparators)
    }

    @Suppress("UNCHECKED_CAST")
    open fun getModelClass(adapter: IBaseAdapter<Parent, Model>): Class<Model> {

        @OptIn(InternalAdaptersApi::class)
        return config.modelClass as? Class<Model> ?: throw NotImplementedError(
            "Can not identify model class." +
                    " Please pass it explicitly."
        )
    }

    override fun updateLogic(updateActions: UpdateActions<Parent, Model>): UpdateLogic<Parent, Model> {
        return SortedUpdate(updateActions)
    }

    class Config<Parent, Model>
            where Model : AdapterParentViewModel<out Parent, Parent> {

        @InternalAdaptersApi
        var modelClass: Class<*>? = null
        lateinit var comparator: Comparator<Parent, Model>
        internal val itemComparators: MutableList<ItemComparator<out Parent, Parent, out Model>> =
            mutableListOf()

        @Suppress("UNCHECKED_CAST")
        fun <Item : Parent> addItemComparator(
            comparator: ItemComparator<Item, Parent, AdapterParentViewModel<Item, Parent>>
        ) {
            itemComparators.add(comparator as ItemComparator<out Parent, Parent, Model>)
        }
    }

    override val featureKey: String = "SortFeature"
}

@Suppress("UNCHECKED_CAST")
object Sorting {
    context (AdapterConfig<Parent, Model>) operator fun <Parent,
            Model : VM<Parent>, TConfig : SortFeature.Config<Parent, Model>> invoke(
        config: TConfig.() -> Unit
    ): SortFeature<Parent, Model> {
        return SortFeature<Parent, Model>().also { feature ->
            feature as ConfigurableFeature<Parent, Model, TConfig>
            install(feature, config)
        }
    }
}