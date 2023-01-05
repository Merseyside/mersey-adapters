package com.merseyside.adapters.core.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun <T> runWithDefault(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Default, block)