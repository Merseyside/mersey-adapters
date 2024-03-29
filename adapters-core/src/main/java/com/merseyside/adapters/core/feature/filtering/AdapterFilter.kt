package com.merseyside.adapters.core.feature.filtering

import com.merseyside.adapters.core.config.contract.HasAdapterWorkManager
import com.merseyside.adapters.core.feature.filtering.listManager.Filters
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.merseyLib.kotlin.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class AdapterFilter<Parent, Model : VM<Parent>> : HasAdapterWorkManager, ILogger {

    private var filterCallback: FilterCallback<Model>? = null

    internal val filters: Filters by lazy { mutableMapOf() }
    internal var notAppliedFilters: Filters = mutableMapOf()

    var isFiltered = false
        internal set

    val itemsCount: Int
        get() = provideFilteredList().size

    private val isBind: Boolean
        get() = this::workManager.isInitialized

    override lateinit var workManager: AdapterWorkManager

    internal var provideFullList: () -> List<Model> = { emptyList() }
    internal var provideFilteredList: () -> List<Model> = { provideFullList() }

    internal fun initListProviders(
        fullListProvider: () -> List<Model>,
        filteredListProvider: () -> List<Model>
    ) {
        this.provideFullList = fullListProvider
        this.provideFilteredList = filteredListProvider
    }

    /**
     * If you pass an object as filter be sure isEquals() implemented properly.
     */
    internal open suspend fun addFilter(key: String, filter: Any) {
        val appliedFilter = filters[key]
        if (appliedFilter != filter) {
            notAppliedFilters[key] = filter
        }
    }

    internal open suspend fun removeFilter(key: String) {
        val appliedFilter = filters[key]
        if (isFiltered) {
            if (appliedFilter != null) {
                filters.remove(key)
                notAppliedFilters.remove(key)
                makeAllFiltersNotApplied()
            }
        }
    }

    fun clearFilters() {
        filters.clear()
        notAppliedFilters.clear()
    }

    suspend fun applyFilters(): Boolean {

        isFiltered = if (isFiltered && areFiltersEmpty()) {
            postResult(getAllModels())
            cancelFiltering()
            false
        } else if (notAppliedFilters.isEmpty()) {
            Logger.logErr("AdapterFilter", "No new filters added. Filtering skipped!")
            return false
        } else {
            val filteredModels = filterModels()
            putAppliedFilters()
            postResult(filteredModels)
            true
        }

        return true
    }

    fun getAllModels(): List<Model> {
        return provideFullList()
    }

    private suspend fun filterModels(): List<Model> {
        val canFilterCurrentItems = filters.isNotEmpty() &&
                !notAppliedFilters.keys.any { key -> filters.containsKey(key) }

        return if (canFilterCurrentItems) {
            filter(provideFilteredList(), notAppliedFilters)
        } else {
            makeAllFiltersNotApplied()
            filter(provideFullList(), notAppliedFilters)
        }
    }

    internal open suspend fun cancelFiltering() {}

    internal fun areFiltersEmpty(): Boolean {
        return filters.isEmpty() && notAppliedFilters.isEmpty()
    }

    private suspend fun postResult(models: List<Model>) {
        filterCallback?.onFiltered(models)
    }

    abstract fun filter(model: Model, key: String, filter: Any): Boolean

    internal suspend fun filter(model: Model): Boolean {
        return filter(model, filters)
    }

    internal suspend fun filter(models: List<Model>): List<Model> {
        return filter(models, filters)
    }

    internal open suspend fun filter(model: Model, filters: Filters): Boolean {
        return filters.all { (key, value) ->
            if (model.isFilterable) {
                filter(model, key, value)
            } else true
        }
    }

    private suspend fun filter(models: List<Model>, filters: Filters): List<Model> {
        return models.filter { model ->
            filter(model, filters)
        }
    }

    internal fun putAppliedFilters() {
        filters.putAll(notAppliedFilters)
        notAppliedFilters.clear()
    }

    private fun makeAllFiltersNotApplied() {
        notAppliedFilters = (filters + notAppliedFilters).toMutableMap()
        filters.clear()
    }

    internal fun setFilterCallback(callback: FilterCallback<Model>) {
        this.filterCallback = callback
    }

    private fun isFilterAlreadyExists(key: String): Boolean {
        return filters.containsKey(key)
    }

    fun interface FilterCallback<Model> {
        suspend fun onFiltered(models: List<Model>)
    }

    override val tag = "AdapterFilter"
}

typealias SimpleAdapterFilter = AdapterFilter<Any, VM<Any>>