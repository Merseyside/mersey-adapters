@file:Suppress("UNCHECKED_CAST")

package com.merseyside.adapters.core.feature.expanding

import com.merseyside.adapters.core.feature.expanding.callback.HasOnItemExpandedListener
import com.merseyside.adapters.core.feature.expanding.callback.OnItemExpandedListener
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.modelList.ModelListCallback
import com.merseyside.merseyLib.kotlin.logger.ILogger
import org.jetbrains.annotations.Contract
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class AdapterExpand<Parent, Model>(
    private val modelList: ModelList<Parent, Model>,
    expandableMode: ExpandableMode = ExpandableMode.MULTIPLE,
    isExpandEnabled: Boolean
) : HasOnItemExpandedListener<Parent>, ModelListCallback<Model>,
    ILogger where Model : VM<Parent> {

    override val expandedListeners: MutableList<OnItemExpandedListener<Parent>> by lazy {
        ArrayList()
    }

    private val expandedList: MutableList<ExpandableItem> by lazy { ArrayList() }

    var expandableMode: ExpandableMode = expandableMode
        set(value) {
            if (field != value) {
                field = value

                if (value == ExpandableMode.SINGLE) {
                    if (expandedList.size > 1) {
                        (1 until expandedList.size).forEach { index ->
                            updateItemWithState(expandedList[index], newState = false)
                        }

                        expandedList.removeAll(expandedList.subList(1, expandedList.lastIndex))
                    }
                }
            }
        }

    private var isExpandEnabled: Boolean = isExpandEnabled
        set(value) {
            if (field != value) {
                field = value

                modelList.mapNotNull { it.asExpandable() }
                    .forEach { it.expandState.globalExpandable.value = value }
            }
        }

    init {
        modelList.addModelListCallback(this)
    }

    override fun onInserted(models: List<Model>, position: Int, count: Int) {
        val items = models.filterIsInstance<ExpandableItem>()
        initNewItems(items)
    }

    private fun initNewItems(items: List<ExpandableItem>) {
        items.forEach { item -> item.expandState.expandEvent.observe { changeItemExpandedState(item) } }
        val expandedItems = items.filter { it.isExpanded() }
        notifyItemsExpanded(expandedItems, false)
    }

    override fun onRemoved(models: List<Model>, position: Int, count: Int) {
        val expandableItems = models.mapNotNull { it.asExpandable() }
        expandedList.removeAll(expandableItems)
    }

    override fun onChanged(
        model: Model,
        position: Int,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {}

    override fun onMoved(fromPosition: Int, toPosition: Int) {}

    fun changeItemExpandedState(item: ExpandableItem, isExpandedByUser: Boolean = true) {
        with(item) {
            if (canItemBeExpanded(item)) {
                when (expandableMode) {
                    ExpandableMode.SINGLE -> {
                        if (!isExpanded()) {
                            val expandedItem = expandedList.firstOrNull()
                            if (expandedItem != null) {
                                updateItemWithState(expandedItem, isExpandedByUser = false)
                            }
                        }

                        updateItemWithState(item, isExpandedByUser = isExpandedByUser)
                    }

                    ExpandableMode.MULTIPLE -> {
                        updateItemWithState(item, isExpandedByUser = isExpandedByUser)
                    }
                }
            }
        }
    }

    private fun updateItemWithState(
        item: ExpandableItem,
        newState: Boolean = !item.isExpanded(),
        isExpandedByUser: Boolean = false
    ): Boolean {
        return if (item.isExpanded() xor newState) {
            item.expandState.expanded = newState
            if (newState) {
                expandedList.add(item)
            } else {
                expandedList.remove(item)
            }

            notifyItemExpanded(item, isExpandedByUser)
            true
        } else false
    }

    fun canItemBeExpanded(item: ExpandableItem): Boolean {
        return isExpandEnabled && item.expandState.expandable
    }

    private fun notifyItemsExpanded(items: List<ExpandableItem>, isExpandedByUser: Boolean) {
        items.forEach { notifyItemExpanded(it, isExpandedByUser) }
    }

    private fun notifyItemExpanded(item: ExpandableItem, isExpandedByUser: Boolean) {
        notifyOnExpanded(item.asModel().item, item.isExpanded(), isExpandedByUser)
    }

    @ExperimentalContracts
    @Contract
    internal fun Model?.isExpandable(): Boolean {
        contract {
            returns(true) implies (this@isExpandable != null && this@isExpandable is ExpandableItem)
        }

        return this != null && this is ExpandableItem
    }

    internal fun Model?.asExpandable(): ExpandableItem? {
        return if (this != null) {
            this as? ExpandableItem
        } else null
    }

    internal fun Model?.requireExpandable(): ExpandableItem {
        return if (this != null) {
            (this as? ExpandableItem) ?: throw IllegalArgumentException("Selectable item required!")
        } else throw NullPointerException()
    }

    private fun ExpandableItem.asModel(): Model {
        return this as Model
    }

    override val tag: String = "AdapterExpand"

}