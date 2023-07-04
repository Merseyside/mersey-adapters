package com.merseyside.adapters.core.feature.sorting.comparator

import com.merseyside.adapters.core.model.VM

interface ComparatorProvider<Parent, Model>
        where Model : VM<Parent> {

    val comparator: Comparator<Parent, Model>
}