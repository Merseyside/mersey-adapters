package com.merseyside.adapters.core.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun <T> runForUI(block: suspend CoroutineScope.() -> T) =
    runBlocking(Dispatchers.Main.immediate, block)

internal suspend fun <T> runWithDefault(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Default, block)