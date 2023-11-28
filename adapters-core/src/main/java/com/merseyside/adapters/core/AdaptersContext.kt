package com.merseyside.adapters.core

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object AdaptersContext {
    var coroutineContext: CoroutineContext = Dispatchers.Default
}