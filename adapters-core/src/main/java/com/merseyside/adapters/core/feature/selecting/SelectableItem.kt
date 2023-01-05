package com.merseyside.adapters.core.feature.selecting

interface SelectableItem {

    val selectState: SelectState

    fun isSelected(): Boolean = selectState.selected
}