package com.merseyside.adapters.delegates.feature.placeholder.comparator

import com.merseyside.adapters.core.feature.sorting.comparator.ItemComparator
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.logger.log

class PlaceholderItemComparator<Item : Parent, Parent, Model>(modelClass: Class<*>) :
    ItemComparator<Item, Parent, Model>(modelClass)
        where Model : AdapterParentViewModel<Item, Parent> {

    override fun compare(model1: Model, model2: VM<Parent>): Int {
        return -1 // Always the first
    }
}