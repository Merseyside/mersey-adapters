package com.merseyside.adapters.core.feature.filtering

import com.merseyside.adapters.core.feature.filtering.listManager.Filters
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.merseyLib.kotlin.extensions.isNotZero

abstract class NestedAdapterFilter<Parent, Model> : AdapterFilter<Parent, Model>()
    where Model : NestedAdapterParentViewModel<out Parent, Parent, *> {

    internal lateinit var getAdapterFilterByModel: suspend (Model) -> AdapterFilter<*, *>?

    final override suspend fun addFilter(key: String, filter: Any) {
        super.addFilter(key, filter)
        val fullList = provideFullList()
        fullList.forEach { model ->
            val filterable = getAdapterFilterByModel(model)
            filterable?.addFilter(key, filter)
        }
    }

    final override suspend fun removeFilter(key: String) {
        super.removeFilter(key)
        val fullList = provideFullList()
        fullList.forEach { model ->
            val adapterFilter = getAdapterFilterByModel(model)
            adapterFilter?.removeFilter(key)
        }
    }

    final override suspend fun filter(model: Model, filters: Filters): Boolean {
        val isFiltered = super.filter(model, filters)

        return if (!isFiltered) return false
        else {
            val adapterFilter = getAdapterFilterByModel(model) ?: return false
            workManager.subTaskForResult(adapterFilter) {
                applyFilters()
                filter(model, itemsCount.isNotZero())
            }
        }
    }

    open fun filter(model: Model, hasItems: Boolean): Boolean = hasItems

    internal suspend fun initAdapterFilter(adapterFilter: AdapterFilter<*, *>) {
        filters.forEach { (key, value) ->
            adapterFilter.addFilter(key, value)
        }

        adapterFilter.applyFilters()
    }

    override suspend fun cancelFiltering() {
        super.cancelFiltering()
        provideFullList()
            .mapNotNull { model -> getAdapterFilterByModel(model) }
            .forEach { adapterFilter -> adapterFilter.applyFilters() }
    }

}