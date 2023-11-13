@file:OptIn(InternalAdaptersApi::class)

package com.merseyside.adapters.compose.view.base.model

import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.merseyLib.kotlin.logger.log

abstract class ViewVM<View : SCV>(view: View): AdapterParentViewModel<View, SCV>(view) {

    init {
        addOnClickListener { item -> "here".log()
            view.clickListeners.log("kek", "listeners")
            item.notifyOnClick(item) }
    }
}

abstract class NestedViewVM<View : SCV>(view: View): NestedAdapterParentViewModel<View, SCV, SCV>(view) {
    init {
        addOnClickListener { item -> item.notifyOnClick(item) }
    }
}