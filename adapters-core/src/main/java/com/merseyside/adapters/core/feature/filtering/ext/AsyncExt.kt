package com.merseyside.adapters.core.feature.filtering.ext

import com.merseyside.adapters.core.feature.filtering.AdapterFilter
import com.merseyside.adapters.core.feature.filtering.AdapterQueryFilter


/**
 * @return true if filters applied, false otherwise.
 */
fun AdapterFilter<*, *>.applyFiltersAsync(onComplete: (Boolean) -> Unit = {}) {
    workManager.doAsync(
        onComplete,
        onError = {
            putAppliedFilters()
            isFiltered = true
            onComplete(false)
        }
    ) { applyFilters() }
}

fun AdapterQueryFilter<*, *>.setQueryAsync(query: String?, onComplete: (Boolean) -> Unit = {}) {
    workManager.doAsync(onComplete) { setQuery(query) }
}