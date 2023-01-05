package com.merseyside.adapters.core.holder

import androidx.databinding.ViewDataBinding
import com.merseyside.adapters.core.model.AdapterParentViewModel

open class TypedBindingHolder<T: AdapterParentViewModel<*, *>>(binding: ViewDataBinding)
    : BaseBindingHolder(binding) {

    @Suppress("UNCHECKED_CAST")
    override val model: T
        get() = super.model as T
}
