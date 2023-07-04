package com.merseyside.adapters.core.feature.sorting.async

import com.merseyside.adapters.core.feature.sorting.comparator.Comparator

fun Comparator<*, *>.updateAsync(onComplete: (Unit) -> Unit = {}) {
    workManager.doAsync(onComplete) { update() }
}