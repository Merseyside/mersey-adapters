package com.merseyside.adapters.delegates.feature.placeholder.resolver

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.CompositeAdapter

/**
 * Handles model list data changes only.
 * Shows placeholder if data is already empty and
 * removes placeholder before some non empty data <b>will</b> be added.
 */
class EmptyDataResolver<Parent, ParentModel : VM<Parent>>(
    private val showPlaceholderOnAttach: Boolean
) : PlaceholderDataResolver<Parent, ParentModel>() {

    override fun getPlaceholderPosition(adapter: CompositeAdapter<Parent, out ParentModel>): Int {
        return LAST_POSITION
    }

    override fun onAdapterAttached(adapter: CompositeAdapter<Parent, out ParentModel>) {
        super.onAdapterAttached(adapter)
        if (showPlaceholderOnAttach) {
            if (isEmpty()) addPlaceholderAsync()
        }
    }

    override suspend fun onInsert(models: List<ParentModel>, count: Int) {
        if (isPlaceholderAdded && count > 0) {
            removePlaceholder()
        }
    }

    override suspend fun onRemoved(models: List<ParentModel>, position: Int, count: Int) {
        if (!isPlaceholderAdded && isEmpty()) {
            addPlaceholder()
        }
    }

    override suspend fun onCleared() {
        if (!isPlaceholderAdded) {
            addPlaceholder()
        }
    }

    override val tag: String = "EmptyDataResolver"
}