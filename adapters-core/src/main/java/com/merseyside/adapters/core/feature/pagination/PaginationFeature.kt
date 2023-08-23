package com.merseyside.adapters.core.feature.pagination

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.feature.ConfigurableFeature
import com.merseyside.adapters.core.feature.dataProvider.AddDataObserver
import com.merseyside.adapters.core.feature.dataProvider.DataObserver
import com.merseyside.adapters.core.feature.dataProvider.DataProvider
import com.merseyside.adapters.core.feature.dataProvider.dataProvider
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.utils.safeLet
import kotlinx.coroutines.flow.Flow

class PaginationFeature<Parent, Model : VM<Parent>> :
    ConfigurableFeature<Parent, Model, Config<Parent, Model>>() {

    override lateinit var config: Config<Parent, Model>

    override fun prepare(configure: Config<Parent, Model>.() -> Unit) {
        config = Config(configure)
    }

    override fun install(
        adapterConfig: AdapterConfig<Parent, Model>,
        adapter: IBaseAdapter<Parent, Model>
    ) {
        super.install(adapterConfig, adapter)

        with(config) {
            val nextPageProvider = DataProvider(adapter, onNextPageFlow, observeWhenAttached)
            nextPageProvider.observeForever(nextPageDataObserver)

            safeLet(onPrevPageFlow) { onPrevFlow ->
                val prevPageProvider = DataProvider(adapter, onPrevFlow, observeWhenAttached)
                prevPageProvider.observeForever(prevPageDataObserver)
            }
        }
    }

    override val featureKey: String = KEY

    companion object {
        const val KEY = "paginationFeature"
    }
}

open class Config<Parent, Model>(
    configure: Config<Parent, Model>.() -> Unit
) where Model : VM<Parent> {

    lateinit var lifecycleOwner: LifecycleOwner

    lateinit var onNextPageFlow: Flow<List<*>>
    var onPrevPageFlow: Flow<List<*>>? = null

    var nextPageDataObserver: DataObserver<out Any, Parent> = AddDataObserver()
    var prevPageDataObserver: DataObserver<out Any, Parent> = AddDataObserver(addToStart = true)

    var observeWhenAttached: Boolean = true

    init {
        apply(configure)
    }

}

object Pagination {
    context (AdapterConfig<Parent, Model>)
    operator fun <Parent, Model : VM<Parent>> invoke(config: Config<Parent, Model>.() -> Unit)
            : PaginationFeature<Parent, Model> {
        return PaginationFeature<Parent, Model>().also { feature ->
            install(feature, config)
        }
    }
}