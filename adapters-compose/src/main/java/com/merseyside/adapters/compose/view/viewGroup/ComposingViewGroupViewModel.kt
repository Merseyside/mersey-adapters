package com.merseyside.adapters.compose.view.viewGroup

import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel

abstract class ComposingViewGroupViewModel<L : ViewGroup>(group: L) :
    NestedAdapterParentViewModel<L, SCV, SCV>(group) {

    override fun getNestedData() = null
}