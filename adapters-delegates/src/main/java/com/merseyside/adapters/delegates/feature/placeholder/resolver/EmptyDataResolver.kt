package com.merseyside.adapters.delegates.feature.placeholder.resolver

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.CompositeAdapter

/**
 * Handles model list data changes only.
 * Shows placeholder if data is already empty and
 * removes placeholder before some non empty data <b>will</b> be added.
 */
class EmptyDataResolver<Parent, ParentModel : VM<Parent>>(
    private val showPlaceholderOnAttach: Boolean,
    private val ignoreClear: Boolean
) : PlaceholderDataResolver<Parent, ParentModel>() {

    override fun onAdapterAttached(adapter: CompositeAdapter<Parent, out ParentModel>) {
        super.onAdapterAttached(adapter)
        if (showPlaceholderOnAttach) {
            if (isEmpty()) addPlaceholderAsync()
        }
    }

    override suspend fun onModelListChanged(oldSize: Int, newSize: Int, hasChanges: Boolean) {
        if (hasChanges || ignoreClear) {
            if (newSize == 0 && (!ignoreClear || oldSize == 0)) addPlaceholder()
            else removePlaceholder()
        }
    }

    override val tag: String = "EmptyDataResolver"
}