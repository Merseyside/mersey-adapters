package com.merseyside.adapters.core.feature.sorting.comparator

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.logger.log
import com.merseyside.merseyLib.kotlin.utils.safeLet

class ComparatorManager<Parent, Model : VM<Parent>>(
    private val comparator: Comparator<Parent, Model>,
    private val itemComparators: List<ItemComparator<out Parent, Parent, out Model>>
) : Comparator<Parent, Model>() {

    override fun compare(model1: Model, model2: Model): Int {
        val responsibleComparator = itemComparators.find { comparator ->
            comparator.isResponsibleFor(model1) || comparator.isResponsibleFor(model2)
        }

        return safeLet(responsibleComparator) { comparator ->
            comparator.compareParents(model1, model2)
        } ?: comparator.compare(model1, model2)
    }

    override fun getModelClass(): Class<*> {
        return comparator.getModelClass()
    }
}