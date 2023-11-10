package com.merseyside.adapters.compose.manager

import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.delegates.DelegateAdapter
import com.merseyside.adapters.delegates.manager.DelegatesManager
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.base.StyleableComposingView
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.Delegate

@Suppress("UNCHECKED_CAST")
class ViewDelegatesManager<Parent : SCV, Model>(
    delegates: List<Delegate<Parent, Model>> = emptyList()
) : DelegatesManager<Parent, Model>(delegates)
        where Model : VM<Parent> {

    override fun getResponsibleDelegate(item: Parent): Delegate<Parent, Model>? {
        return super.getResponsibleDelegate(item) ?: getDelegateFromView(item)
    }

    private fun getDelegateFromView(item: Parent): Delegate<Parent, Model> {
        return (item.delegate as DelegateAdapter<out Parent, Parent, Model>).also { delegate ->
            addDelegates(delegate)
        }
    }

    override fun getChildDelegatesManager(): ViewDelegatesManager<Parent, Model> {
        return ViewDelegatesManager(getDelegates())
    }
}

