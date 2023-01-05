package com.merseyside.adapters.core.nested

import com.merseyside.adapters.core.base.AdapterActions
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel

interface NestedAdapterActions<Parent, Model, InnerData, InnerAdapter>
    : AdapterActions<Parent, Model>
        where Model : NestedAdapterParentViewModel<out Parent, Parent, InnerData>,
              InnerAdapter : BaseAdapter<InnerData, out AdapterParentViewModel<out InnerData, InnerData>> {

    fun getNestedAdapterByModel(model: Model): InnerAdapter?

    fun initNestedAdapterByModel(model: Model): InnerAdapter

    fun removeNestedAdapterByModel(model: Model): Boolean
}