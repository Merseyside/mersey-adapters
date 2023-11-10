package com.merseyside.adapters.core.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * Use run blocking to prevent suspend
 */
fun <T> runForUI(block: suspend CoroutineScope.() -> T) =
    runBlocking(Dispatchers.Main.immediate, block)