package com.merseyside.adapters.core.model.ext

import com.merseyside.adapters.core.model.VM

fun <Parent, Model : VM<Parent>> List<Model>.toItems(): List<Parent> {
    return map { it.item }
}