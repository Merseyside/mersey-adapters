package com.merseyside.adapters.delegates.feature.placeholder.resolver

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.CompositeAdapter

class AlwaysVisibleDataResolver<Parent, ParentModel : VM<Parent>>(
    private val showPlaceholderOnAttach: Boolean = false
) : PlaceholderDataResolver<Parent, ParentModel>() {

    override fun onAdapterAttached(adapter: CompositeAdapter<Parent, out ParentModel>) {
        super.onAdapterAttached(adapter)
        if (showPlaceholderOnAttach) {
            if (!isPlaceholderAdded) addPlaceholderAsync()
        }
    }

    override suspend fun onModelListChanged(oldSize: Int, newSize: Int, hasChanges: Boolean) {
        if (!isPlaceholderAdded) addPlaceholder()
    }
}