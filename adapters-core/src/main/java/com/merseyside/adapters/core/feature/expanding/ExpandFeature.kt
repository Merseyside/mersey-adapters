package com.merseyside.adapters.core.feature.expanding

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.NestedAdapterConfig
import com.merseyside.adapters.core.config.feature.ConfigurableFeature
import com.merseyside.adapters.core.config.feature.NestedConfigurableFeature
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.nested.INestedAdapter


class ExpandFeature<Parent, Model, InnerData, InnerAdapter> :
    NestedConfigurableFeature<Parent, Model, InnerData, InnerAdapter, Config<Parent, Model>>()
        where Model : NestedAdapterParentViewModel<out Parent, Parent, InnerData>,
              InnerAdapter : BaseAdapter<InnerData, *> {

    override lateinit var config: Config<Parent, Model>
    lateinit var adapterExpand: AdapterExpand<Parent, Model>

    override fun prepare(configure: Config<Parent, Model>.() -> Unit) {
        config = Config(configure)
    }

    override fun install(
        adapterConfig: NestedAdapterConfig<Parent, Model, InnerData, InnerAdapter>,
        adapter: INestedAdapter<Parent, Model, InnerData, InnerAdapter>
    ) {
        with(config) {
            adapterExpand = AdapterExpand(
                adapterConfig.modelList,
                expandableMode,
                isExpandedEnabled
            )
        }
    }

    override val featureKey: String = KEY

    companion object {
        const val KEY = "ExpandingFeature"
    }
}

class Config<Parent, Model>(
    configure: Config<Parent, Model>.() -> Unit
) where Model : VM<Parent> {

    var expandableMode: ExpandableMode = ExpandableMode.MULTIPLE
    var isExpandedEnabled: Boolean = true

    init {
        apply(configure)
    }
}

object Expanding {
    context (NestedAdapterConfig<Parent, Model, InnerData, InnerAdapter>) @Suppress("UNCHECKED_CAST")
    operator fun
            <Parent, Model, InnerData, InnerAdapter, TConfig> invoke(
        config: TConfig.() -> Unit = {}
    ): ExpandFeature<Parent, Model, InnerData, InnerAdapter>
            where Model : NestedAdapterParentViewModel<out Parent, Parent, InnerData>,
                  InnerAdapter : BaseAdapter<InnerData, *>,
                  TConfig : Config<Parent, Model> {
        return ExpandFeature<Parent, Model, InnerData, InnerAdapter>().also { feature ->
            feature as ConfigurableFeature<Parent, Model, TConfig>
            install(feature, config)
        }
    }
}