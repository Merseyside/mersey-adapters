package com.merseyside.adapters.delegates.feature.placeholder.resolver.state

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.resolver.PlaceholderDataResolver

abstract class StateDataResolver<StateData, Parent, ParentModel : VM<Parent>>: PlaceholderDataResolver<Parent, ParentModel>() {

    fun addStateData(stateData: StateData) {
        onStateData(stateData, adapter)
    }

    protected abstract fun onStateData(
        stateData: StateData,
        adapter: CompositeAdapter<Parent, out ParentModel>
    )

}