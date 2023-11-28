package com.merseyside.adapters.core.async

import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.model.VM

fun <Parent, Model : VM<Parent>, Result> IBaseAdapter<Parent, Model>.doBlocking(
    work: suspend IBaseAdapter<Parent, Model>.() -> Result,
) {
    return workManager.doBlocking { work() }
}