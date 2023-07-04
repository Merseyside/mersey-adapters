package com.merseyside.adapters.compose.feature.sorting

import com.merseyside.adapters.compose.model.ViewAdapterViewModel
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.utils.reflection.ReflectionUtils
import com.merseyside.adapters.core.feature.sorting.comparator.Comparator

abstract class ViewComparator<Model : ViewAdapterViewModel> : Comparator<SCV, ViewAdapterViewModel>() {

    @Suppress("UNCHECKED_CAST")
    override fun compare(
        model1: ViewAdapterViewModel,
        model2: ViewAdapterViewModel
    ): Int {
        return compareViews(model1 as Model, model2 as Model)
    }

    abstract fun compareViews(model1: Model, model2: Model): Int

    @Suppress("UNCHECKED_CAST")
    override fun getModelClass(): Class<Model> {
        return ReflectionUtils.getGenericParameterClass(
            this.javaClass,
            ViewComparator::class.java,
            0
        ) as Class<Model>
    }
}