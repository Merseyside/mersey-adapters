package com.merseyside.adapters.delegates.feature.placeholder.resolver.state

import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.entity.result.Result
import com.merseyside.merseyLib.kotlin.entity.result.isEmpty
import com.merseyside.merseyLib.kotlin.logger.Logger
import kotlinx.coroutines.flow.Flow

/**
 * Receives Result and adds placeholder on success
 * if adapter's data is empty and data in result is empty too.
 * Removes placeholder in other cases.
 */
open class ResultDataResolver<Parent, ParentModel : VM<Parent>>(
    viewLifecycleOwner: LifecycleOwner,
    flow: Flow<Result<*>>
) : StateDataResolver<Result<*>, Parent, ParentModel>(viewLifecycleOwner, flow) {

    private var currentResult: Result<*> = Result.NotInitialized<Any>()

    override val tag: String = "ResultDataResolver"
    override fun onDataProvided(
        adapter: IBaseAdapter<Parent, *>,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") stateData: Result<*>
    ) {
        when (stateData) {
            is Result.Loading -> {
                if (isPlaceholderAdded) removePlaceholderAsync()
            }

            is Result.Success -> {
                if (adapter.isEmpty() && stateData.isEmpty()) addPlaceholderAsync()
            }

            is Result.Error -> {
                Logger.logErr(tag, "Error state, skip placeholder")
            }

            else -> {}
        }
        currentResult = stateData
    }
}