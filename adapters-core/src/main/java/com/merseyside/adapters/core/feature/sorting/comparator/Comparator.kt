package com.merseyside.adapters.core.feature.sorting.comparator

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.utils.reflection.ReflectionUtils

abstract class Comparator<Parent, Model : VM<Parent>>(
    protected var animation: Boolean = true
) {

    private lateinit var callback: OnComparatorUpdateCallback
    internal lateinit var workManager: AdapterWorkManager

    abstract fun compare(model1: Model, model2: Model): Int

    suspend fun update() {
        callback.onUpdate(animation)
    }

    internal fun setOnComparatorUpdateCallback(callback: OnComparatorUpdateCallback) {
        this.callback = callback
    }

    internal interface OnComparatorUpdateCallback {
        suspend fun onUpdate(animation: Boolean)
    }

    open fun getModelClass(): Class<*> {
        return ReflectionUtils.getGenericParameterClass(
            this.javaClass,
            Comparator::class.java,
            1
        )
    }
}

typealias SimpleComparator = Comparator<Any, VM<Any>>