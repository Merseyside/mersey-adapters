package com.merseyside.adapters.core.config.contract

import com.merseyside.adapters.core.feature.filtering.AdapterFilter
import com.merseyside.adapters.core.model.VM

interface FilterProvider<Parent, Model>
        where Model : VM<Parent> {

    val adapterFilter: AdapterFilter<Parent, Model>
}