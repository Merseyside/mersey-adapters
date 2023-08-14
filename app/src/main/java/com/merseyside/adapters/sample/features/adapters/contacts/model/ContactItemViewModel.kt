package com.merseyside.adapters.sample.features.adapters.contacts.model

import com.merseyside.adapters.core.feature.selecting.SelectState
import com.merseyside.adapters.core.feature.selecting.SelectableItem
import com.merseyside.adapters.core.model.AdapterViewModel
import com.merseyside.merseyLib.kotlin.logger.log

class ContactItemViewModel(item: String, override val selectState: SelectState) :
    AdapterViewModel<String>(item), SelectableItem {

    init {
        selectState.setOnSelectStateListener(object : SelectState.OnSelectStateListener {
            override fun onSelected(selected: Boolean) {
                selected.log("select", "selected")
            }

            override fun onSelectable(selectable: Boolean) {}
        })
    }

    override val id: Any = item

    override fun areItemsTheSame(other: String): Boolean {
        return item == other
    }
}