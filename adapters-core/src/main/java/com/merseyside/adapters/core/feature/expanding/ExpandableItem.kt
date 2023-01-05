package com.merseyside.adapters.core.feature.expanding

interface ExpandableItem {

    val expandState: ExpandState

    fun isExpanded(): Boolean = expandState.expanded
}