package com.merseyside.adapters.core

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object AdaptersContext {
    var coroutineContext: CoroutineContext = EmptyCoroutineContext
}