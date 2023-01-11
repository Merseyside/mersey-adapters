package com.merseyside.adapters.core.config

import com.merseyside.adapters.core.AdaptersContext
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.contract.ModelListProvider
import com.merseyside.adapters.core.config.contract.UpdateLogicProvider
import com.merseyside.adapters.core.config.ext.getFeatureByKey
import com.merseyside.adapters.core.config.ext.hasFeature
import com.merseyside.adapters.core.config.feature.ConfigurableFeature
import com.merseyside.adapters.core.config.feature.Feature
import com.merseyside.adapters.core.config.update.simple.SimpleUpdate
import com.merseyside.adapters.core.feature.filtering.FilterFeature
import com.merseyside.adapters.core.feature.filtering.listManager.FilterModelListManager
import com.merseyside.adapters.core.listManager.IModelListManager
import com.merseyside.adapters.core.listManager.impl.ModelListManager
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.modelList.ModelListCallback
import com.merseyside.adapters.core.modelList.SimpleModelList
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.coroutines.queue.CoroutineQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class AdapterConfig<Parent, Model> internal constructor(
    config: AdapterConfig<Parent, Model>.() -> Unit = {}
)
        where Model : VM<Parent> {
    protected lateinit var adapter: IBaseAdapter<Parent, Model>

    internal val featureList = ArrayList<Feature<Parent, Model>>()

    private lateinit var _modelListManager: IModelListManager<Parent, Model>
    open val listManager: IModelListManager<Parent, Model>
        get() = _modelListManager

    lateinit var modelList: ModelList<Parent, Model>

    var errorHandler: ((Exception) -> Unit)? = null

    var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    var coroutineContext: CoroutineContext = AdaptersContext.coroutineContext

    internal val workManager: AdapterWorkManager

    init {
        apply(config)
        if (errorHandler == null) errorHandler = { e -> throw e }
        workManager = AdapterWorkManager(
            CoroutineQueue<Any, Unit>(coroutineScope).apply { fallOnException = true },
            coroutineContext,
            errorHandler!!
        )
    }

    fun install(feature: Feature<Parent, Model>) {
        featureList.add(feature)
    }

    fun <TConfig : Any> install(
        feature: ConfigurableFeature<Parent, Model, TConfig>,
        config: TConfig.() -> Unit
    ) {
        feature.prepare(config)
        featureList.add(feature)
    }

    open fun initAdapterWithConfig(adapter: IBaseAdapter<Parent, Model>) {
        this.adapter = adapter
        adapter.workManager = workManager

        initModelListManager(adapter)

        featureList
            .filter { !it.isInstalled }
            .forEach { feature -> feature.install(this, adapter) }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun initModelList(listCallback: ModelListCallback<Model>): ModelList<Parent, Model> {
        val listProviders: List<ModelListProvider<Parent, Model>> =
            featureList.filterIsInstance<ModelListProvider<Parent, Model>>()

        if (listProviders.size > 1) throw IllegalArgumentException(
            "There are few list provider features. Have to be zero or one"
        )

        return if (listProviders.isEmpty()) {
            SimpleModelList()
        } else {
            val listProvider = listProviders.first()
            val listProviderFeature = listProvider as Feature<Parent, Model>
            listProviderFeature.install(this, adapter)

            listProvider.modelList

        }.also { modelList ->
            modelList.addModelListCallback(listCallback)
            this.modelList = modelList
        }
    }

    fun initWithUpdateLogic(listChangeDelegate: IModelListManager<Parent, Model>) {
        val updateLogic = featureList.filterIsInstance<UpdateLogicProvider<Parent, Model>>()
            .firstOrNull()?.updateLogic(listChangeDelegate) ?: SimpleUpdate(listChangeDelegate)

        listChangeDelegate.updateLogic = updateLogic
    }

    @Suppress("UNCHECKED_CAST")
    open fun initModelListManager(adapter: IBaseAdapter<Parent, Model>): IModelListManager<Parent, Model> {
        if (!this::_modelListManager.isInitialized) {
            _modelListManager = if (hasFeature(FilterFeature.key)) {
                val filterFeature =
                    getFeatureByKey(FilterFeature.key) as FilterFeature<Parent, Model>
                FilterModelListManager(
                    modelList = initModelList(adapter),
                    adapterActions = adapter,
                    adapterFilter = filterFeature.adapterFilter,
                    workManager = workManager
                )
            } else {
                ModelListManager(
                    modelList = initModelList(adapter),
                    adapterActions = adapter,
                    workManager = workManager
                )
            }.also { initWithUpdateLogic(it) }
        }

        return _modelListManager
    }
}