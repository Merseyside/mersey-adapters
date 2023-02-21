package com.merseyside.adapters.core.feature.selecting

import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.feature.ConfigurableFeature
import com.merseyside.adapters.core.feature.selecting.callback.onItemSelected
import com.merseyside.adapters.core.model.VM


class SelectFeature<Parent, Model> : ConfigurableFeature<Parent, Model, Config<Parent, Model>>(),
    SelectProvider<Parent, Model>
        where Model : VM<Parent> {

    override lateinit var config: Config<Parent, Model>
    override val featureKey: String = KEY

    override lateinit var adapterSelect: AdapterSelect<Parent, Model>

    override fun prepare(configure: Config<Parent, Model>.() -> Unit) {
        config = Config(configure)
    }

    override fun install(
        adapterConfig: AdapterConfig<Parent, Model>,
        adapter: IBaseAdapter<Parent, Model>
    ) {
        super.install(adapterConfig, adapter)

        with(config) {
            adapterSelect = AdapterSelect(
                adapterConfig.modelList,
                selectableMode,
                isSelectEnabled,
                isAllowToCancelSelection,
                forceSelect,
                adapterConfig.workManager
            )

            adapterSelect.onItemSelected(onSelect)
        }
    }

    companion object {
        const val KEY = "SelectFeature"
    }
}

class Config<Parent, Model>(
    configure: Config<Parent, Model>.() -> Unit = {}
) where Model : VM<Parent> {
    var selectableMode: SelectableMode = SelectableMode.SINGLE
    var forceSelect: Boolean = true
    var isSelectEnabled: Boolean = true
    private var _isAllowToCancelSelection: Boolean? = null
        get() = field ?: (selectableMode == SelectableMode.MULTIPLE)

    var isAllowToCancelSelection: Boolean
        set(value) {
            _isAllowToCancelSelection = value
        }
        get() = _isAllowToCancelSelection!!

    var onSelect: (
    item: Parent,
    isSelected: Boolean,
    isSelectedByUser: Boolean
    ) -> Unit = { _, _, _ ->  }

    init {
        apply(configure)
    }
}

@Suppress("UNCHECKED_CAST")
object Selecting {
    context (AdapterConfig<Parent, Model>) operator fun <Parent,
            Model : VM<Parent>, TConfig : Config<Parent, Model>> invoke(
        config: TConfig.() -> Unit = {}
    ): SelectFeature<Parent, Model> {
        return SelectFeature<Parent, Model>().also { feature ->
            feature as ConfigurableFeature<Parent, Model, TConfig>
            install(feature, config)
        }
    }
}