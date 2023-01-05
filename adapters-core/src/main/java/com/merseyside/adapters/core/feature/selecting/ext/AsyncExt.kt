package com.merseyside.adapters.core.feature.selecting.ext

import com.merseyside.adapters.core.feature.selecting.AdapterSelect


fun AdapterSelect<*, *>.clearAsync(onComplete: (Unit) -> Unit = {}) {
    workManager.doAsync(onComplete) { clear() }
}