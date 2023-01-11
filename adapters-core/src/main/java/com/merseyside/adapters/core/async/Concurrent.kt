package com.merseyside.adapters.core.async

import com.merseyside.merseyLib.kotlin.coroutines.utils.uiDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun <T> runForUI(block: suspend CoroutineScope.() -> T) = withContext(uiDispatcher, block)

internal suspend fun <T> runWithDefault(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Default, block)