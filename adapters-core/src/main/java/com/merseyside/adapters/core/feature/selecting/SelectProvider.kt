package com.merseyside.adapters.core.feature.selecting

import com.merseyside.adapters.core.model.VM


interface SelectProvider<Parent, Model>
        where Model : VM<Parent> {

    val adapterSelect: AdapterSelect<Parent, Model>
}