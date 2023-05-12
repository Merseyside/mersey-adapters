package com.merseyside.adapters.core.feature.pagination

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.feature.ConfigurableFeature
import com.merseyside.adapters.core.model.VM
import kotlinx.coroutines.flow.Flow

class PaginationFeature<Parent, Model : VM<Parent>> : ConfigurableFeature<Parent, Model, Config<Parent, Model>>() {

    override lateinit var config: Config<Parent, Model>
    internal lateinit var adapterPagination: AdapterPagination<Parent>

    override fun prepare(configure: Config<Parent, Model>.() -> Unit) {
        config = Config(configure)
    }

    override val featureKey: String = KEY

    override fun install(
        adapterConfig: AdapterConfig<Parent, Model>,
        adapter: IBaseAdapter<Parent, Model>
    ) {
        super.install(adapterConfig, adapter)

        with(config) {
            adapterPagination = AdapterPagination(adapter, viewLifecycleOwner, onNextPage, onPrevPage)
        }
    }

    companion object {
        const val KEY = "paginationFeature"
    }
}

open class Config<Parent, Model>(
    configure: Config<Parent, Model>.() -> Unit = {}
) where Model : VM<Parent> {

    lateinit var viewLifecycleOwner: LifecycleOwner

    lateinit var onNextPage: Flow<List<Parent>>
    var onPrevPage: Flow<List<Parent>>? = null

    init {
        apply(configure)
    }

}

object Pagination {
    context (AdapterConfig<Parent, Model>) @Suppress("UNCHECKED_CAST")
    operator fun <Parent,
            Model : VM<Parent>, TConfig : Config<Parent, Model>> invoke(
        config: TConfig.() -> Unit
    ): PaginationFeature<Parent, Model> {
        return PaginationFeature<Parent, Model>().also { feature ->
            feature as ConfigurableFeature<Parent, Model, TConfig>
            install(feature, config)
        }
    }
}