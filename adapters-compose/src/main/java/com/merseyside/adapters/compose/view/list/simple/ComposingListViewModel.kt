package com.merseyside.adapters.compose.view.list.simple

import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.base.model.NestedViewVM

open class ComposingListViewModel<L : ComposingList>(list: L) :
    NestedViewVM<L>(list) {
    override fun getNestedData(): List<SCV>? = null
}