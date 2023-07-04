package com.merseyside.adapters.compose.manager

import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.delegates.DelegateAdapter
import com.merseyside.adapters.delegates.manager.DelegatesManager
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.model.VM
import com.merseyside.utils.ext.values

@Suppress("UNCHECKED_CAST")
class ViewDelegatesManager<Parent : SCV, Model>(
    delegates: List<DelegateAdapter<out Parent, Parent, Model>> = emptyList()
) : DelegatesManager<DelegateAdapter<out Parent, Parent, Model>, Parent, Model>(delegates)
        where Model : VM<Parent> {

    override fun getResponsibleDelegate(item: Parent): DelegateAdapter<out Parent, Parent, Model>? {
        return super.getResponsibleDelegate(item)
    }

    fun getAllDelegates(): List<ViewDelegateAdapter<out Parent, *, *>> {
        return delegates.values() as List<ViewDelegateAdapter<out Parent, *, *>>
    }
}