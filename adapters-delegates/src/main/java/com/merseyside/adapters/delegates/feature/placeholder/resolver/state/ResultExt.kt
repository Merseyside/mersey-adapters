package com.merseyside.adapters.delegates.feature.placeholder.resolver.state

import com.merseyside.merseyLib.kotlin.entity.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

fun <Data> ResultDataResolver<Data, *, *>.collectState(
    coroutineScope: CoroutineScope,
    flow: Flow<Result<List<Data>>>
) {
    coroutineScope.launch {
        flow
            .onEach { addStateData(it) }
            .launchIn(coroutineScope)
    }
}