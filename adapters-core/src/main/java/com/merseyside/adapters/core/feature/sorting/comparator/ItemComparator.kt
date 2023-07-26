package com.merseyside.adapters.core.feature.sorting.comparator

import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.logger.log
import com.merseyside.merseyLib.kotlin.utils.safeLet
import com.merseyside.utils.reflection.ReflectionUtils

abstract class ItemComparator<Item : Parent, Parent, Model : AdapterParentViewModel<Item, Parent>> {

    private var modelClass: Class<*>? = null
        get() = field ?: getPersistentClass().also { field = it }

    constructor(modelClass: Class<*>): super() {
        this.modelClass = modelClass
    }

    internal fun compareParents(model1: VM<Parent>, model2: VM<Parent>): Int {
        return if (isResponsibleFor(model1)) {
            compare(model1 as Model, model2)
        } else {
            -compare(model2 as Model, model1)
        }
    }

    abstract fun compare(model1: Model, model2: VM<Parent>): Int

    fun isResponsibleFor(model: VM<Parent>) : Boolean {
        return safeLet(model) {
            return it::class.java == modelClass
        } ?: false
    }

    private fun getPersistentClass(): Class<*> {
        return ReflectionUtils.getGenericParameterClass(
            this.javaClass,
            ItemComparator::class.java,
            0
        )
    }
}

interface HasItemComparator<Parent, Model : VM<Parent>> {
    fun getItemComparator(): ItemComparator<out Parent, Parent, out Model>
}