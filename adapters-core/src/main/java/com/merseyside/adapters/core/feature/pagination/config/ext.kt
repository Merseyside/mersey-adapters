package com.merseyside.adapters.core.feature.pagination.config

import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.feature.pagination.AdapterPagination
import com.merseyside.adapters.core.feature.pagination.PaginationFeature
import com.merseyside.adapters.core.model.VM

fun <Parent, Model : VM<Parent>> AdapterConfig<Parent, Model>.getAdapterPagination(): AdapterPagination<Parent>? {
    return featureList.filterIsInstance<PaginationFeature<Parent, Model>>()
        .firstOrNull()?.adapterPagination
}