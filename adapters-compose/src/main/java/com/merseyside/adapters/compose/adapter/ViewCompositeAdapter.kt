package com.merseyside.adapters.compose.adapter

import com.merseyside.adapters.compose.manager.ViewDelegatesManager
import com.merseyside.adapters.compose.model.ViewAdapterViewModel
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.viewGroup.ViewGroup
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.init.initAdapter
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.CompositeAdapter

open class ViewCompositeAdapter<Parent, Model>(
    adapterConfig: AdapterConfig<Parent, Model>,
    override val delegatesManager: ViewDelegatesManager<Parent, Model> = ViewDelegatesManager()
) : CompositeAdapter<Parent, Model>(adapterConfig, delegatesManager)
        where Parent : SCV, Model : VM<Parent> {

    @Suppress("UNCHECKED_CAST")
    fun <View : SCV> findViewById(id: String): View? {
        models.forEach { model ->
            val view = model.item
            val foundView = if (view is ViewGroup) view.findViewById(id)
            else if (view.id == id) view as View
            else null

            if (foundView != null) return foundView
        }

        return null
    }

    companion object {
        operator fun <Parent : SCV, Model> invoke(
            delegatesManager: ViewDelegatesManager<Parent, Model>,
            configure: AdapterConfig<Parent, Model>.() -> Unit
        ): ViewCompositeAdapter<Parent, Model>
                where Model : VM<Parent> {
            return initAdapter(::ViewCompositeAdapter, delegatesManager, configure)
        }
    }
}

typealias SimpleViewCompositeAdapter = ViewCompositeAdapter<SCV, ViewAdapterViewModel>