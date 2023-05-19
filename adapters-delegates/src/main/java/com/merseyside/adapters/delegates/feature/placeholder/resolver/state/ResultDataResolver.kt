package com.merseyside.adapters.delegates.feature.placeholder.resolver.state

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.merseyLib.kotlin.entity.result.Result
import com.merseyside.merseyLib.kotlin.logger.Logger

/**
 * Receives Result and adds placeholder on success if adapter's data is empty and data in result is empty too.
 * Removes placeholder in other cases.
 */
open class ResultDataResolver<Data, Parent, ParentModel : VM<Parent>> :
    StateDataResolver<Result<List<Data>>, Parent, ParentModel>() {

    private var currentResult: Result<List<Data>> = Result.NotInitialized()

    override fun getPlaceholderPosition(adapter: CompositeAdapter<Parent, out ParentModel>): Int {
        return LAST_POSITION
    }

    override fun onStateData(
        stateData: Result<List<Data>>,
        adapter: CompositeAdapter<Parent, out ParentModel>
    ) {
        when (stateData) {
            is Result.Loading -> {
                if (isPlaceholderAdded) removePlaceholderAsync()
            }

            is Result.Success -> {
                if (adapter.isEmpty() && stateData.value.isEmpty()) addPlaceholderAsync()
            }

            is Result.Error -> {
                Logger.logErr(tag, "Error state, skip placeholder")
            }

            else -> {}
        }
        currentResult = stateData
    }

    override val tag: String = "ResultDataResolver"
}